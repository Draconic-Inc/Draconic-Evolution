package com.brandon3055.draconicevolution.integration.computers;

/**
 * Created by brandon3055 on 23/03/2017.
 * This is just a basic argument helper based on OC's Arguments interface.
 * I built this so that it can be used with both CC and OC.
 * I will implement the rest of the methods if i ever need them.
 */
public class ArgHelper {

    private final Object[] args;

    public ArgHelper(Object[] args) {
        this.args = args;
    }

    /**
     * The total number of arguments that were passed to the function.
     */
    public int count() {
        return args.length;
    }

    /**
     * Get whatever is at the specified index.
     * <p/>
     * Throws an error if there are too few arguments.
     * <p/>
     * The returned object will be one of the following, based on the conversion
     * performed internally:
     * <ul>
     * <li><tt>null</tt> if the Lua value was <tt>nil</tt>.</li>
     * <li><tt>java.lang.Boolean</tt> if the Lua value was a boolean.</li>
     * <li><tt>java.lang.Double</tt> if the Lua value was a number.</li>
     * <li><tt>byte[]</tt> if the Lua value was a string.</li>
     * </ul>
     *
     * @param index the index from which to get the argument.
     * @return the raw value at that index.
     * @throws IllegalArgumentException if there is no argument at that index.
     */
    public Object checkAny(int index) {
        if (index >= count()) {
            throw new IllegalArgumentException("Expected argument at index: " + index);
        }

        return args[index];
    }

    /**
     * Try to get a boolean value at the specified index.
     * <p/>
     * Throws an error if there are too few arguments.
     *
     * @param index the index from which to get the argument.
     * @return the boolean value at the specified index.
     * @throws IllegalArgumentException if there is no argument at that index,
     *                                  or if the argument is not a boolean.
     */
    public boolean checkBoolean(int index) {
        if (index >= count()) {
            throw new IllegalArgumentException("Expected boolean at index: " + index);
        }
        else if (args[index] instanceof Boolean) {
            return (Boolean) args[index];
        }
        throw new IllegalArgumentException("Expected boolean at index: " + index);
    }

    /**
     * Try to get an integer value at the specified index.
     * <p/>
     * Throws an error if there are too few arguments.
     *
     * @param index the index from which to get the argument.
     * @return the integer value at the specified index.
     * @throws IllegalArgumentException if there is no argument at that index,
     *                                  or if the argument is not a number.
     */
    public int checkInteger(int index) {
        if (index >= count()) {
            throw new IllegalArgumentException("Expected integer at index: " + index);
        }
        else if (args[index] instanceof Double) {
            double d = (Double) args[index];
            return (int) d;
        }
        throw new IllegalArgumentException("Expected integer at index: " + index);
    }

    public long checkLong(int index) {
        if (index >= count()) {
            throw new IllegalArgumentException("Expected long at index: " + index);
        }
        else if (args[index] instanceof Double) {
            double d = (Double) args[index];
            return (long) d;
        }
        throw new IllegalArgumentException("Expected long at index: " + index);
    }

    /**
     * Try to get a double value at the specified index.
     * <p/>
     * Throws an error if there are too few arguments.
     *
     * @param index the index from which to get the argument.
     * @return the double value at the specified index.
     * @throws IllegalArgumentException if there is no argument at that index,
     *                                  or if the argument is not a number.
     */
    public double checkDouble(int index) {
        if (index >= count()) {
            throw new IllegalArgumentException("Expected double at index: " + index);
        }
        else if (args[index] instanceof Double) {
            return (Double) args[index];
        }
        throw new IllegalArgumentException("Expected double at index: " + index);
    }

//    /**
//     * Try to get a string value at the specified index.
//     * <p/>
//     * Throws an error if there are too few arguments.
//     * <p/>
//     * This will actually check for a byte array and convert it to a string
//     * using UTF-8 encoding.
//     *
//     * @param index the index from which to get the argument.
//     * @return the boolean value at the specified index.
//     * @throws IllegalArgumentException if there is no argument at that index,
//     *                                  or if the argument is not a string.
//     */
//    public String checkString(int index) {
//        checkAny(index);
//    }
//
//    /**
//     * Try to get a byte array at the specified index.
//     * <p/>
//     * Throws an error if there are too few arguments.
//     *
//     * @param index the index from which to get the argument.
//     * @return the byte array at the specified index.
//     * @throws IllegalArgumentException if there is no argument at that index,
//     *                                  or if the argument is not a byte array.
//     */
//    public byte[] checkByteArray(int index) {
//        checkAny(index);
//        return new byte[0];
//    }
//
//    /**
//     * Try to get a table at the specified index.
//     * <p/>
//     * Throws an error if there are too few arguments.
//     *
//     * @param index the index from which to get the argument.
//     * @return the table at the specified index.
//     * @throws IllegalArgumentException if there is no argument at that index,
//     *                                  or if the argument is not a table.
//     */
//    public Map checkTable(int index) {
//        checkAny(index);
//        return null;
//    }
//
//    /**
//     * Try to get an item stack representation at the specified index.
//     * <p/>
//     * This is a utility method provided to convert tables to item stacks, with
//     * the tables being of a compatible format to that of tables generated by
//     * the built-in item stack converter. In particular, this takes care of
//     * restoring NBT data attached to the item stack.
//     * <p/>
//     * Throws an error if there are too few arguments.
//     * <p/>
//     * <em>Important</em>: usually you will not want to be using this. Some
//     * items require NBT information to fully describe them, and by default
//     * this information is not returned to underlying architectures when
//     * item stacks are returned from callbacks. This means the scripts can
//     * usually not provide this full information, so the roundtrip callback->
//     * script->callback will be incomplete.
//     * <p/>
//     * Instead, please make use of the {@link li.cil.oc.api.internal.Database}
//     * component to get complete item stack descriptors.
//     *
//     * @param index the index from which to get the argument.
//     * @return the item stack at the specified index.
//     */
//    public ItemStack checkItemStack(int index) {
//        checkAny(index);
//        return null;
//    }
//
//    /**
//     * Get whatever is at the specified index.
//     * <p/>
//     * Return the specified default value if there is no such element, behaves
//     * like {@link #checkAny(int)} otherwise.
//     * <p/>
//     * The returned object will be one of the following, based on the conversion
//     * performed internally:
//     * <ul>
//     * <li><tt>null</tt> if the Lua value was <tt>nil</tt>.</li>
//     * <li><tt>java.lang.Boolean</tt> if the Lua value was a boolean.</li>
//     * <li><tt>java.lang.Double</tt> if the Lua value was a number.</li>
//     * <li><tt>byte[]</tt> if the Lua value was a string.</li>
//     * </ul>
//     *
//     * @param index the index from which to get the argument.
//     * @return the raw value at that index.
//     */
//    public Object optAny(int index, Object def) {
//        return null;
//    }
//
//    /**
//     * Try to get a boolean value at the specified index.
//     * <p/>
//     * Return the specified default value if there is no such element, behaves
//     * like {@link #checkBoolean(int)} otherwise.
//     *
//     * @param index the index from which to get the argument.
//     * @return the boolean value at the specified index.
//     * @throws IllegalArgumentException if the argument exists and is not a boolean.
//     */
//    boolean optBoolean(int index, boolean def) {
//        return false;
//    }
//
//    /**
//     * Try to get an integer value at the specified index.
//     * <p/>
//     * Return the specified default value if there is no such element, behaves
//     * like {@link #checkInteger(int)} otherwise.
//     *
//     * @param index the index from which to get the argument.
//     * @return the integer value at the specified index.
//     * @throws IllegalArgumentException if the argument exists but is not a number.
//     */
//    public int optInteger(int index, int def) {
//        return 0;
//    }
//
//    /**
//     * Try to get a double value at the specified index.
//     * <p/>
//     * Return the specified default value if there is no such element, behaves
//     * like {@link #checkDouble(int)} otherwise.
//     *
//     * @param index the index from which to get the argument.
//     * @return the double value at the specified index.
//     * @throws IllegalArgumentException if the argument exists and is not a number.
//     */
//    public double optDouble(int index, double def) {
//        return 0;
//    }
//
//    /**
//     * Try to get a string value at the specified index.
//     * <p/>
//     * Return the specified default value if there is no such element, behaves
//     * like {@link #checkString(int)} otherwise.
//     * <p/>
//     * This will actually check for a byte array and convert it to a string
//     * using UTF-8 encoding.
//     *
//     * @param index the index from which to get the argument.
//     * @return the boolean value at the specified index.
//     * @throws IllegalArgumentException if the argument exists and is not a string.
//     */
//    public String optString(int index, String def) {
//        return null;
//    }
//
//    /**
//     * Try to get a byte array at the specified index.
//     * <p/>
//     * Return the specified default value if there is no such element, behaves
//     * like {@link #checkByteArray(int)} otherwise.
//     *
//     * @param index the index from which to get the argument.
//     * @return the byte array at the specified index.
//     * @throws IllegalArgumentException if the argument exists and is not a byte array.
//     */
//    public byte[] optByteArray(int index, byte[] def) {
//        return new byte[0];
//    }
//
//    /**
//     * Try to get a table at the specified index.
//     * <p/>
//     * Return the specified default value if there is no such element, behaves
//     * like {@link #checkTable(int)} otherwise.
//     *
//     * @param index the index from which to get the argument.
//     * @return the table at the specified index.
//     * @throws IllegalArgumentException if the argument exists and is not a table.
//     */
//    public Map optTable(int index, Map def) {
//        return null;
//    }
//
//    /**
//     * Try to get an item stack at the specified index.
//     * <p/>
//     * Return the specified default value if there is no such element, behaves
//     * like {@link #checkItemStack(int)} otherwise.
//     *
//     * @param index the index from which to get the argument.
//     * @return the item stack at the specified index.
//     * @throws IllegalArgumentException if the argument exists and is not an item stack.
//     */
//    public ItemStack optItemStack(int index, ItemStack def) {
//        return null;
//    }
//
//    /**
//     * Tests whether the argument at the specified index is a boolean value.
//     * <p/>
//     * This will return false if there is <em>no</em> argument at the specified
//     * index, i.e. if there are too few arguments.
//     *
//     * @param index the index to check.
//     * @return true if the argument is a boolean; false otherwise.
//     */
//    public boolean isBoolean(int index) {
//        return false;
//    }
//
//    /**
//     * Tests whether the argument at the specified index is an integer value.
//     * <p/>
//     * This will return false if there is <em>no</em> argument at the specified
//     * index, i.e. if there are too few arguments.
//     *
//     * @param index the index to check.
//     * @return true if the argument is an integer; false otherwise.
//     */
//    public boolean isInteger(int index) {
//        return false;
//    }
//
//    /**
//     * Tests whether the argument at the specified index is a double value.
//     * <p/>
//     * This will return false if there is <em>no</em> argument at the specified
//     * index, i.e. if there are too few arguments.
//     *
//     * @param index the index to check.
//     * @return true if the argument is a double; false otherwise.
//     */
//    public boolean isDouble(int index) {
//        return false;
//    }
//
//    /**
//     * Tests whether the argument at the specified index is a string value.
//     * <p/>
//     * This will return false if there is <em>no</em> argument at the specified
//     * index, i.e. if there are too few arguments.
//     *
//     * @param index the index to check.
//     * @return true if the argument is a string; false otherwise.
//     */
//    public boolean isString(int index) {
//        return false;
//    }
//
//    /**
//     * Tests whether the argument at the specified index is a byte array.
//     * <p/>
//     * This will return false if there is <em>no</em> argument at the specified
//     * index, i.e. if there are too few arguments.
//     *
//     * @param index the index to check.
//     * @return true if the argument is a byte array; false otherwise.
//     */
//    public boolean isByteArray(int index) {
//        return false;
//    }
//
//    /**
//     * Tests whether the argument at the specified index is a table.
//     * <p/>
//     * This will return false if there is <em>no</em> argument at the specified
//     * index, i.e. if there are too few arguments.
//     *
//     * @param index the index to check.
//     * @return true if the argument is a table; false otherwise.
//     */
//    public boolean isTable(int index) {
//        return false;
//    }
//
//    /**
//     * Tests whether the argument at the specified index is an item stack.
//     * <p/>
//     * This will return false if there is <em>no</em> argument at the specified
//     * index, i.e. if there are too few arguments.
//     *
//     * @param index the index to check.
//     * @return true if the argument is an item stack; false otherwise.
//     */
//    public boolean isItemStack(int index) {
//        return false;
//    }

    /**
     * Converts the argument list to a standard Java array, converting byte
     * arrays to strings automatically, since this is usually what others
     * want - if you need the actual raw byte arrays, don't use this method!
     *
     * @return an array containing all arguments.
     */
    public Object[] toArray() {
        return args;
    }

}
