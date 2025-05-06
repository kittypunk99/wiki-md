package org.trottlinc.letztesding;

public class WikiConverter {

    private static final String HTML_HEADER = "<html><head><meta charset='UTF-8'></head><body>";
    private static final String HTML_FOOTER = "</body></html>";

    public static String convertToHtml(String input) {
        StringBuilder sb = new StringBuilder();
        sb.append(HTML_HEADER);
        String[] lines = input.split("\\r?\\n");
        boolean inList = false;
        int prevListLevel = 0;
        boolean inTable = false;
        boolean isHeaderRow = true;
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) {
                if (inList) {
                    sb.append("</ul>".repeat(prevListLevel));
                    inList = false;
                    prevListLevel = 0;
                }
                if (inTable) {
                    sb.append("</table>");
                    inTable = false;
                    isHeaderRow = true;
                }
                sb.append("<p></p>");
                continue;
            }
            if (line.matches("-{4,}")) {
                sb.append("<hr/>");
                continue;
            }
            if (line.startsWith("|")) {
                if (!inTable) {
                    inTable = true;
                    sb.append("<table border='1'>");
                }
                String[] cells = line.substring(1).split("\\|");
                StringBuilder row = new StringBuilder("<tr>");
                for (String cell : cells) {
                    if (isHeaderRow) {
                        row.append("<th>").append(convertInline(cell.trim())).append("</th>");
                    } else {
                        row.append("<td>").append(convertInline(cell.trim())).append("</td>");
                    }
                }
                row.append("</tr>");
                sb.append(row);
                isHeaderRow = false;
                continue;
            } else if (inTable) {
                sb.append("</table>");
                inTable = false;
                isHeaderRow = true;
            }
            if (line.startsWith("!!!!!!")) sb.append(convertHeading(line).trim());
            else if (line.startsWith("!!!!!")) sb.append(convertHeading(line).trim());
            else if (line.startsWith("!!!!")) sb.append(convertHeading(line).trim());
            else if (line.startsWith("!!!")) sb.append(convertHeading(line).trim());
            else if (line.startsWith("!!")) sb.append(convertHeading(line).trim());
            else if (line.startsWith("!")) sb.append(convertHeading(line).trim());
            else if (line.startsWith("*")||line.startsWith("#")) {
                int level = line.startsWith("*")?countLeadingChar(line, '*'):countLeadingChar(line, '#');
                if (!inList) {
                    inList = true;
                    sb.append("<ul>");
                } else if (level > prevListLevel) {
                    sb.append("<ul>".repeat(level - prevListLevel));
                } else if (level < prevListLevel) {
                    sb.append("</ul>".repeat(prevListLevel - level));
                }
                sb.append("<li>").append(convertInline(line.substring(level).trim())).append("</li>");
                prevListLevel = level;
            } else {
                if (inList) {
                    sb.append("</ul>".repeat(prevListLevel));
                    inList = false;
                    prevListLevel = 0;
                }
                sb.append("<p>").append(convertInline(line)).append("</p>");
            }
        }
        if (inList) sb.append("</ul>".repeat(prevListLevel));
        if (inTable) sb.append("</table>");
        sb.append(HTML_FOOTER);
        return sb.toString();
    }

    private static int countLeadingChar(String line, char c) {
        int count = 0;
        while (count < line.length() && line.charAt(count) == c) count++;
        return count;
    }

    private static String wrap(String tag, String content) {
        return "<" + tag + ">" + convertInline(content) + "</" + tag + ">";
    }

    private static String convertInline(String text) {
        text = text.replaceAll("''(.*?)''", "<b>$1</b>");
        text = text.replaceAll("//(.*?)//", "<i>$1</i>");
        text = text.replaceAll("\\[(.+?)]\\((https?://\\S+)\\)", "<a href=\"$2\">$1</a>");
        text = text.replaceAll("\\[(.+?)]\\(#([a-zA-Z0-9\\-_]+)\\)", "<a href=\"#$2\">$1</a>");
        return text;
    }
    private static String convertHeading(String line) {
        int level = 0;
        while (line.startsWith("!")) {
            level++;
            line = line.substring(1);
        }
        String text = convertInline(line.trim());
        String id = line.toLowerCase().replaceAll("[^a-z0-9]+", "");
        return String.format("<h%d id=\"%s\">%s</h%d>", level, id, text, level);
    }

}
