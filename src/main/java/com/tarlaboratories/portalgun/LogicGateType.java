package com.tarlaboratories.portalgun;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

import net.minecraft.util.StringRepresentable;

public enum LogicGateType implements StringRepresentable {
    OR("or", (List<Boolean> input) -> input.contains(true)),
    AND("and", (List<Boolean> input) -> !input.contains(false)),
    XOR("xor", (List<Boolean> input) -> input.contains(true) && input.contains(false)),
    RAND("rand", (List<Boolean> input) -> input.get(((new Random()).nextInt(input.size()))));

    private final String name;
    private final Function<List<Boolean>, Boolean> func;

    private LogicGateType(String name, Function<List<Boolean>, Boolean> func) {
        this.name = name;
        this.func = func;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public Boolean applyFunction(List<Boolean> input) {
        return func.apply(input);
    }
}
