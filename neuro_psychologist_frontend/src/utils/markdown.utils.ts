/**
 * Converts markdown text to HTML
 * Supports: bold text, lists, line breaks
 */
export const markdownToHtml = (markdown: string): string => {
  if (!markdown) return '';

  let html = markdown;

  // Convert **bold** to <strong>
  html = html.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>');

  // Convert bullet points (- item) to list items
  const lines = html.split('\n');
  let inList = false;
  const processedLines: string[] = [];

  for (let i = 0; i < lines.length; i++) {
    const line = lines[i];
    const trimmedLine = line.trim();

    // Check if line starts with "- "
    if (trimmedLine.startsWith('- ')) {
      if (!inList) {
        processedLines.push('<ul class="markdown-list">');
        inList = true;
      }
      const content = trimmedLine.substring(2).trim();
      processedLines.push(`<li>${content}</li>`);
    } else {
      if (inList) {
        processedLines.push('</ul>');
        inList = false;
      }
      
      // Add line break for non-empty lines
      if (trimmedLine) {
        processedLines.push(line);
      } else if (i < lines.length - 1) {
        // Add spacing between paragraphs
        processedLines.push('<br/>');
      }
    }
  }

  // Close list if still open
  if (inList) {
    processedLines.push('</ul>');
  }

  html = processedLines.join('\n');

  // Wrap paragraphs
  html = html.replace(/^(?!<[uo]l|<li|<br\/>)(.+)$/gm, '<p>$1</p>');

  return html;
};