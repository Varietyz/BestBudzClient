package com.bestbudz.dock.definitions;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ItemBonusXMLLoader {

	private static final String ROOT_ELEMENT = "ItemBonusDefinitions";
	private static final String DEFINITION_ELEMENT = "ItemBonusDefinition";
	private static final String ID_ELEMENT = "id";
	private static final String BONUSES_ELEMENT = "bonuses";
	private static final String BONUS_ELEMENT = "short";

	private static final int MAX_ITEMS_TO_LOAD = 50000;
	private static final int MAX_BONUSES_PER_ITEM = 50;
	private static final int MIN_EXPECTED_BONUSES = 1;

	public ItemBonusLoadResult loadFromResource(String resourcePath) {
		if (resourcePath == null || resourcePath.trim().isEmpty()) {
			return ItemBonusLoadResult.failure("Resource path cannot be null or empty");
		}

		System.out.println("[ItemBonusXMLLoader] Loading definitions from: " + resourcePath);

		try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
			if (inputStream == null) {
				return ItemBonusLoadResult.failure("Resource not found: " + resourcePath);
			}

			return loadFromInputStream(inputStream, resourcePath);

		} catch (IOException e) {
			return ItemBonusLoadResult.failure("IO error loading resource: " + e.getMessage());
		} catch (Exception e) {
			return ItemBonusLoadResult.failure("Unexpected error: " + e.getMessage());
		}
	}

	public ItemBonusLoadResult loadFromInputStream(InputStream inputStream, String sourceName) {
		if (inputStream == null) {
			return ItemBonusLoadResult.failure("InputStream cannot be null");
		}

		long startTime = System.currentTimeMillis();
		List<ItemBonusDefinition> definitions = new ArrayList<>();
		List<String> warnings = new ArrayList<>();

		try {

			DocumentBuilderFactory factory = createSecureDocumentBuilderFactory();
			DocumentBuilder builder = factory.newDocumentBuilder();

			Document document = builder.parse(inputStream);
			document.getDocumentElement().normalize();

			Element root = document.getDocumentElement();
			if (!isValidRootElement(root)) {
				return ItemBonusLoadResult.failure(
					"Invalid XML structure. Expected root element '" + ROOT_ELEMENT +
						"' or '" + DEFINITION_ELEMENT + "', found: " + root.getNodeName()
				);
			}

			NodeList definitionNodes = getDefinitionNodes(root);
			int processedCount = parseDefinitions(definitionNodes, definitions, warnings);

			long duration = System.currentTimeMillis() - startTime;

			System.out.println("[ItemBonusXMLLoader] ✅ Loaded " + definitions.size() +
				" definitions from " + processedCount + " entries in " + duration + "ms");

			if (!warnings.isEmpty()) {
				System.out.println("[ItemBonusXMLLoader] ⚠️ " + warnings.size() + " warnings during parsing");
				for (String warning : warnings) {
					System.out.println("[ItemBonusXMLLoader] Warning: " + warning);
				}
			}

			return ItemBonusLoadResult.success(definitions, warnings);

		} catch (ParserConfigurationException e) {
			return ItemBonusLoadResult.failure("XML parser configuration error: " + e.getMessage());
		} catch (SAXException e) {
			return ItemBonusLoadResult.failure("XML parsing error: " + e.getMessage());
		} catch (IOException e) {
			return ItemBonusLoadResult.failure("IO error reading XML: " + e.getMessage());
		} catch (Exception e) {
			return ItemBonusLoadResult.failure("Unexpected error parsing XML: " + e.getMessage());
		}
	}

	private DocumentBuilderFactory createSecureDocumentBuilderFactory() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
		factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		factory.setXIncludeAware(false);
		factory.setExpandEntityReferences(false);

		return factory;
	}

	private boolean isValidRootElement(Element root) {
		String rootName = root.getNodeName();
		return ROOT_ELEMENT.equals(rootName) || DEFINITION_ELEMENT.equals(rootName);
	}

	private NodeList getDefinitionNodes(Element root) {
		if (DEFINITION_ELEMENT.equals(root.getNodeName())) {

			return new SingleNodeList(root);
		} else {

			return root.getElementsByTagName(DEFINITION_ELEMENT);
		}
	}

	private int parseDefinitions(NodeList definitionNodes, List<ItemBonusDefinition> definitions, List<String> warnings) {
		int processedCount = 0;
		int maxItems = Math.min(definitionNodes.getLength(), MAX_ITEMS_TO_LOAD);

		for (int i = 0; i < maxItems; i++) {
			Node node = definitionNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				processedCount++;

				try {
					ItemBonusDefinition definition = parseDefinition((Element) node);
					if (definition != null) {
						String validationError = definition.validate();
						if (validationError == null) {
							definitions.add(definition);
						} else {
							warnings.add("Invalid definition at index " + i + ": " + validationError);
						}
					} else {
						warnings.add("Failed to parse definition at index " + i);
					}
				} catch (Exception e) {
					warnings.add("Error parsing definition at index " + i + ": " + e.getMessage());
				}
			}
		}

		if (definitionNodes.getLength() > MAX_ITEMS_TO_LOAD) {
			warnings.add("Truncated loading at " + MAX_ITEMS_TO_LOAD + " items (found " + definitionNodes.getLength() + ")");
		}

		return processedCount;
	}

	private ItemBonusDefinition parseDefinition(Element element) {
		try {

			int itemId = parseItemId(element);
			if (itemId < 0) {
				return null;
			}

			short[] bonuses = parseBonuses(element);
			if (bonuses == null || bonuses.length < MIN_EXPECTED_BONUSES) {
				return null;
			}

			return new ItemBonusDefinition(itemId, bonuses);

		} catch (Exception e) {
			System.err.println("[ItemBonusXMLLoader] Error parsing definition: " + e.getMessage());
			return null;
		}
	}

	private int parseItemId(Element element) {
		try {
			NodeList idNodes = element.getElementsByTagName(ID_ELEMENT);
			if (idNodes.getLength() == 0) {
				System.err.println("[ItemBonusXMLLoader] No id element found in definition");
				return -1;
			}

			String idText = idNodes.item(0).getTextContent().trim();
			return Integer.parseInt(idText);

		} catch (NumberFormatException e) {
			System.err.println("[ItemBonusXMLLoader] Invalid item ID format: " + e.getMessage());
			return -1;
		}
	}

	private short[] parseBonuses(Element element) {
		try {
			NodeList bonusesNodes = element.getElementsByTagName(BONUSES_ELEMENT);
			if (bonusesNodes.getLength() == 0) {
				return null;
			}

			Element bonusesElement = (Element) bonusesNodes.item(0);
			NodeList bonusNodes = bonusesElement.getElementsByTagName(BONUS_ELEMENT);

			if (bonusNodes.getLength() == 0) {
				return null;
			}

			if (bonusNodes.getLength() > MAX_BONUSES_PER_ITEM) {
				System.err.println("[ItemBonusXMLLoader] Too many bonuses (" + bonusNodes.getLength() +
					"), truncating to " + MAX_BONUSES_PER_ITEM);
			}

			int bonusCount = Math.min(bonusNodes.getLength(), MAX_BONUSES_PER_ITEM);
			short[] bonuses = new short[bonusCount];

			for (int i = 0; i < bonusCount; i++) {
				String bonusText = bonusNodes.item(i).getTextContent().trim();
				try {
					bonuses[i] = Short.parseShort(bonusText);
				} catch (NumberFormatException e) {
					System.err.println("[ItemBonusXMLLoader] Invalid bonus value at index " + i + ": " + bonusText);
					bonuses[i] = 0;
				}
			}

			return bonuses;

		} catch (Exception e) {
			System.err.println("[ItemBonusXMLLoader] Error parsing bonuses: " + e.getMessage());
			return null;
		}
	}

	private static class SingleNodeList implements NodeList {
		private final Node node;

		public SingleNodeList(Node node) {
			this.node = node;
		}

		@Override
		public Node item(int index) {
			return index == 0 ? node : null;
		}

		@Override
		public int getLength() {
			return 1;
		}
	}
}
