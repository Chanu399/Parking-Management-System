package com.nibm.parking.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A minimal JSON reader/writer with no external dependencies.
 * Supports Map (JSON object), List (JSON array), String, Number, Boolean and null.
 * This is not a general-purpose JSON library - just enough to act as this
 * project's lightweight "database" file format.
 */
public class SimpleJson {

    // ---------- WRITER ----------

    public static String toJson(Object value) {
        StringBuilder sb = new StringBuilder();
        write(value, sb, 0);
        return sb.toString();
    }

    private static void write(Object value, StringBuilder sb, int indent) {
        if (value == null) {
            sb.append("null");
        } else if (value instanceof String) {
            writeString((String) value, sb);
        } else if (value instanceof Number || value instanceof Boolean) {
            sb.append(value.toString());
        } else if (value instanceof Map) {
            writeMap((Map<?, ?>) value, sb, indent);
        } else if (value instanceof List) {
            writeList((List<?>) value, sb, indent);
        } else {
            writeString(value.toString(), sb);
        }
    }

    private static void writeMap(Map<?, ?> map, StringBuilder sb, int indent) {
        if (map.isEmpty()) {
            sb.append("{}");
            return;
        }
        sb.append("{\n");
        int i = 0;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            indent(sb, indent + 1);
            writeString(String.valueOf(entry.getKey()), sb);
            sb.append(": ");
            write(entry.getValue(), sb, indent + 1);
            if (++i < map.size()) {
                sb.append(",");
            }
            sb.append("\n");
        }
        indent(sb, indent);
        sb.append("}");
    }

    private static void writeList(List<?> list, StringBuilder sb, int indent) {
        if (list.isEmpty()) {
            sb.append("[]");
            return;
        }
        sb.append("[\n");
        for (int i = 0; i < list.size(); i++) {
            indent(sb, indent + 1);
            write(list.get(i), sb, indent + 1);
            if (i < list.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        indent(sb, indent);
        sb.append("]");
    }

    private static void indent(StringBuilder sb, int level) {
        for (int i = 0; i < level; i++) {
            sb.append("  ");
        }
    }

    private static void writeString(String s, StringBuilder sb) {
        sb.append('"');
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
    }

    // ---------- PARSER ----------

    public static Object parse(String json) {
        Parser parser = new Parser(json);
        Object value = parser.parseValue();
        parser.skipWhitespace();
        return value;
    }

    private static class Parser {
        private final String s;
        private int pos = 0;

        Parser(String s) {
            this.s = s;
        }

        void skipWhitespace() {
            while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) {
                pos++;
            }
        }

        Object parseValue() {
            skipWhitespace();
            char c = s.charAt(pos);
            switch (c) {
                case '{': return parseObject();
                case '[': return parseArray();
                case '"': return parseString();
                case 't': pos += 4; return Boolean.TRUE;
                case 'f': pos += 5; return Boolean.FALSE;
                case 'n': pos += 4; return null;
                default: return parseNumber();
            }
        }

        Map<String, Object> parseObject() {
            Map<String, Object> map = new LinkedHashMap<>();
            pos++; // consume '{'
            skipWhitespace();
            if (s.charAt(pos) == '}') {
                pos++;
                return map;
            }
            while (true) {
                skipWhitespace();
                String key = parseString();
                skipWhitespace();
                pos++; // consume ':'
                Object value = parseValue();
                map.put(key, value);
                skipWhitespace();
                char c = s.charAt(pos);
                if (c == ',') {
                    pos++;
                    continue;
                }
                if (c == '}') {
                    pos++;
                    break;
                }
            }
            return map;
        }

        List<Object> parseArray() {
            List<Object> list = new ArrayList<>();
            pos++; // consume '['
            skipWhitespace();
            if (s.charAt(pos) == ']') {
                pos++;
                return list;
            }
            while (true) {
                Object value = parseValue();
                list.add(value);
                skipWhitespace();
                char c = s.charAt(pos);
                if (c == ',') {
                    pos++;
                    continue;
                }
                if (c == ']') {
                    pos++;
                    break;
                }
            }
            return list;
        }

        String parseString() {
            pos++; // consume opening quote
            StringBuilder sb = new StringBuilder();
            while (s.charAt(pos) != '"') {
                char c = s.charAt(pos);
                if (c == '\\') {
                    pos++;
                    char esc = s.charAt(pos);
                    switch (esc) {
                        case '"': sb.append('"'); break;
                        case '\\': sb.append('\\'); break;
                        case '/': sb.append('/'); break;
                        case 'n': sb.append('\n'); break;
                        case 'r': sb.append('\r'); break;
                        case 't': sb.append('\t'); break;
                        case 'u':
                            String hex = s.substring(pos + 1, pos + 5);
                            sb.append((char) Integer.parseInt(hex, 16));
                            pos += 4;
                            break;
                        default:
                            sb.append(esc);
                    }
                } else {
                    sb.append(c);
                }
                pos++;
            }
            pos++; // consume closing quote
            return sb.toString();
        }

        Object parseNumber() {
            int start = pos;
            while (pos < s.length() && "-+.eE0123456789".indexOf(s.charAt(pos)) >= 0) {
                pos++;
            }
            String numStr = s.substring(start, pos);
            if (numStr.contains(".") || numStr.contains("e") || numStr.contains("E")) {
                return Double.parseDouble(numStr);
            }
            return Long.parseLong(numStr);
        }
    }
}