import os

def save_tree_structure(start_path, exclude_entries=None, output_file="Navigation.md"):
    """
    Recursively generates a tree structure of the directory contents starting from `start_path`
    and writes the output to a Markdown file wrapped in code fences.
    
    The output is enclosed within triple backticks (```) so that it renders as a formatted code block.
    A variety of emojis are used to represent different file types.
    
    Parameters:
    - start_path (str): The starting directory path.
    - exclude_entries (list, optional): Files or directories to exclude.
    - output_file (str, optional): The name of the output Markdown file.
    """
    if exclude_entries is None:
        exclude_entries = [
            "desktop.ini",
            "node_modules", 
            ".git",
            "dist",
            "assets",
            ".venv",
            "Image-ExifTool-13.26",
            "runtime",
            ".gradle",
            ".idea",
            ".vscode",
            "build",
            "gradle"
        ]
    
    # Mapping of file extensions to emojis
    file_emojis = {
        '.py': '🐍',
        '.js': '📜',
        '.json': '🔧',
        '.txt': '📄',
        '.md': '📝',
        '.html': '🌐',
        '.css': '🎨',
        '.jpg': '🖼️',
        '.jpeg': '🖼️',
        '.png': '🖼️',
        '.gif': '🖼️',
        '.ico': '🖼️',
        '.mp3': '🎵',
        '.wav': '🎵',
        '.mp4': '🎞️',
        '.pdf': '📕',
        '.gdoc': '🗄️',
        '.xlsx': '🧮',
        '.psd': '🖌️',
    }
    
    def get_file_emoji(filename):
        """
        Returns an emoji representing the file based on text-based or extension-based mappings.
        """
        filename_lower = filename.lower()
        
        # Mapping for text patterns (keys can be substrings)
        text_mapping = {
            "readme": "📘",
            "license": "⚖️",
            "receipt": "🧾",
            "faq": "❓",
            "rules": "📖",
            "invitation": "💌",
            "agenda": "📅",
            "analytics": "📈",
            "brainstorming": "🧠",
            "insights": "🔎",
            "guidelines": "ℹ️",
            "tools": "🛠️",
            "insights": "👁️",
            "sponsor": "💵",
            "finished": "✅",
            "bot": "🤖",
        }
        
        # Check if any text pattern is present in the filename
        for pattern, emoji in text_mapping.items():
            if pattern in filename_lower:
                return emoji
        
        # Extension-based mapping (assumes file_emojis is defined in the outer scope)
        ext = os.path.splitext(filename)[1].lower()
        return file_emojis.get(ext, '📄')


    
    def walk_directory(directory, depth=0, prefix=""):
        """
        Recursively walks the directory structure, building a list of strings representing
        each file and folder in a tree format.
        
        Parameters:
        - directory (str): The directory to traverse.
        - depth (int): The current recursion depth.
        - prefix (str): The string prefix for the current depth (used for formatting).
        
        Returns:
        - list of str: The formatted lines representing the tree structure.
        """
        if any(excluded in directory for excluded in exclude_entries):
            return []
        
        tree_lines = []
        entries = os.listdir(directory)
        # Filter out entries that are exactly in the exclusion list
        entries = [entry for entry in entries if entry not in exclude_entries]
        total_entries = len(entries)
        
        for idx, entry in enumerate(entries):
            full_path = os.path.join(directory, entry)
            is_last = idx == total_entries - 1
            symbol = "└─" if is_last else "├─"
            new_prefix = prefix + ("    " if is_last else "│   ")
            
            if os.path.isdir(full_path):
                emoji = "📂"
                tree_lines.append(f"{prefix}{symbol} {emoji} {entry}")
                tree_lines.extend(walk_directory(full_path, depth + 1, new_prefix))
            else:
                emoji = get_file_emoji(entry)
                tree_lines.append(f"{prefix}{symbol} {emoji} {entry}")
        
        return tree_lines

    # Generate the directory structure
    directory_structure = walk_directory(start_path)
    # Wrap the structure in Markdown code fences (```)
    markdown_output = "```\n" + "\n".join(directory_structure) + "\n```"
    
    # Write the Markdown output to the specified file using UTF-8 encoding
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(markdown_output)
    
    print(f"Folder structure has been saved to {output_file}")

if __name__ == "__main__":
    current_directory = os.getcwd()
    save_tree_structure(current_directory)
