package com.pavengine.app.PavCrypt;

public enum CryptEnum {
    Int(0),
    Float(1),
    String(2),
    Boolean(3),
    Vector2(4),
    Vector3(5),
    Quaternion(6);

    public final int id;
    CryptEnum(int id){ this.id = id; }

    public static CryptEnum fromId(int typeId) {
        for (CryptEnum e : CryptEnum.values()) {
            if (e.id == typeId) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown CryptEnum id: " + typeId);
    }
}
