package img.util;

public enum Variant {
    VT_EMPTY,
    VT_NULL,
    VT_I2,
    VT_I4,
    VT_R4,
    VT_R8,
    VT_CY,
    VT_DATE,
    VT_BSTR,
    VT_DISPATCH,
    VT_ERROR,
    VT_BOOL,
    VT_VARIANT,
    VT_UNKNOWN,
    VT_DECIMAL,
    VT_RESERVED,
    VT_I1,
    VT_UI1,
    VT_UI2,
    VT_UI4,
    VT_I8,
    VT_UI8,
    VT_INT,
    VT_UINT;

    public static Variant fromByte(int ordinal) {
        return Variant.values()[ordinal];
    }

}
