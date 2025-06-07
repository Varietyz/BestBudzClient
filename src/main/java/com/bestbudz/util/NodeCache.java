package com.bestbudz.util;

import com.bestbudz.cache.Signlink;

final class NodeCache {
	private final int size;
	private final Node[] cache;

	public NodeCache() {
		size = 1024;
		cache = new Node[size];
		for (int i = 0; i < size; i++) {
			Node node = cache[i] = new Node();
			node.prev = node;
			node.next = node;
		}
	}

	public Node findNodeByID(long id) {
		Node head = cache[(int) (id & (size - 1))];
		for (Node node = head.prev; node != head; node = node.prev) {
			if (node.id == id) {
				return node;
			}
		}
		return null;
	}

	public void removeFromCache(Node node, long id) {
		if (node.next != null) {
			node.unlink();
		}

		Node head = cache[(int) (id & (size - 1))];
		node.next = head.next;
		node.prev = head;
		node.next.prev = node;
		node.prev.next = node;
		node.id = id;
	}
}
