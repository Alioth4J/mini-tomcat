package server.connector.http;

/**
 * 对应 HTTP 请求头的一个键值对
 */
public class HttpRequestHeader {

    public static final int INITIAL_NAME_SIZE = 64;
    public static final int INITIAL_VALUE_SIZE = 512;
    public static final int MAX_NAME_SIZE = 128;
    public static final int MAX_VALUE_SIZE = 1024;

    public char[] name;
    public int nameEnd;
    public char[] value;
    public int valueEnd;
    protected int hashCode = 0;

    public HttpRequestHeader() {
        this(new char[INITIAL_NAME_SIZE], 0, new char[INITIAL_VALUE_SIZE], 0);
    }

    public HttpRequestHeader(char[] name, int nameEnd,
                      char[] value, int valueEnd) {
        this.name = name;
        this.nameEnd = nameEnd;
        this.value = value;
        this.valueEnd = valueEnd;
    }

    public HttpRequestHeader(String name, String value) {
        this.name = name.toLowerCase().toCharArray();
        this.nameEnd = name.length();
        this.value = value.toLowerCase().toCharArray();
        this.valueEnd = value.length();
    }

    public void recycle() {
        nameEnd = 0;
        valueEnd = 0;
        hashCode = 0;
    }

    public char[] getName() {
        return name;
    }

    public void setName(char[] name) {
        this.name = name;
    }

    public void setNameValue(int index, char value) {
        this.name[index] = value;
    }

    public int getNameEnd() {
        return nameEnd;
    }

    public void setNameEnd(int nameEnd) {
        this.nameEnd = nameEnd;
    }

    public char[] getValue() {
        return value;
    }

    public void setValue(char[] value) {
        this.value = value;
    }

    public void setValueValue(int index, char value) {
        this.value[index] = value;
    }

    public int getValueEnd() {
        return valueEnd;
    }

    public void setValueEnd(int valueEnd) {
        this.valueEnd = valueEnd;
    }

    public int getHashCode() {
        return hashCode;
    }

    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }

}
