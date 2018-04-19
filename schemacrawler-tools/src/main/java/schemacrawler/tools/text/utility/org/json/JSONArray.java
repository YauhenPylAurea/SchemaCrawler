package schemacrawler.tools.text.utility.org.json;


/*
 Copyright (c) 2002 JSON.org

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 The Software shall be used for Good, not Evil.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * A JSONArray is an ordered sequence of values. Its external text form
 * is a string wrapped in square brackets with commas separating the
 * values. The internal form is an object having <code>get</code> and
 * <code>opt</code> methods for accessing the values by index, and
 * <code>put</code> methods for adding or replacing values. The values
 * can be any of these types: <code>Boolean</code>,
 * <code>JSONArray</code>, <code>JSONObject</code>, <code>Number</code>,
 * <code>String</code>, or the <code>JSONObject.NULL object</code>.
 * <p>
 * The constructor can convert a JSON text into a Java object. The
 * <code>toString</code> method converts to JSON text.
 * <p>
 * A <code>get</code> method returns a value if one can be found, and
 * throws an exception if one cannot be found. An <code>opt</code>
 * method returns a default value instead of throwing an exception, and
 * so is useful for obtaining optional values.
 * <p>
 * The generic <code>get()</code> and <code>opt()</code> methods return
 * an object which you can cast or query for type. There are also typed
 * <code>get</code> and <code>opt</code> methods that do type checking
 * and type coercion for you.
 * <p>
 * The texts produced by the <code>toString</code> methods strictly
 * conform to JSON syntax rules. The constructors are more forgiving in
 * the texts they will accept:
 * <ul>
 * <li>An extra <code>,</code>&nbsp;<small>(comma)</small> may appear
 * just before the closing bracket.</li>
 * <li>The <code>null</code> value will be inserted when there is
 * <code>,</code>&nbsp;<small>(comma)</small> elision.</li>
 * <li>Strings may be quoted with <code>'</code>&nbsp;<small>(single
 * quote)</small>.</li>
 * <li>Strings do not need to be quoted at all if they do not begin with
 * a quote or single quote, and if they do not contain leading or
 * trailing spaces, and if they do not contain any of these characters:
 * <code>{ } [ ] / \ : , = ; #</code> and if they do not look like
 * numbers and if they are not the reserved words <code>true</code>,
 * <code>false</code>, or <code>null</code>.</li>
 * <li>Values can be separated by <code>;</code>
 * <small>(semicolon)</small> as well as by <code>,</code>
 * <small>(comma)</small>.</li>
 * <li>Numbers may have the <code>0x-</code> <small>(hex)</small>
 * prefix.</li>
 * </ul>
 *
 * @author JSON.org
 * @version 2011-08-25
 */
public class JSONArray
{

  /**
   * The arrayList where the JSONArray's properties are kept.
   */
  private final ArrayList myArrayList;

  /**
   * Construct an empty JSONArray.
   */
  public JSONArray()
  {
    myArrayList = new ArrayList();
  }

  /**
   * Construct a JSONArray from a Collection.
   *
   * @param collection
   *        A Collection.
   */
  public JSONArray(final Collection collection)
  {
    myArrayList = new ArrayList();
    if (collection != null)
    {
      final Iterator iter = collection.iterator();
      while (iter.hasNext())
      {
        myArrayList.add(JSONObject.wrap(iter.next()));
      }
    }
  }

  /**
   * Construct a JSONArray from an array
   *
   * @throws JSONException
   *         If not an array.
   */
  public JSONArray(final Object array)
    throws JSONException
  {
    this();
    if (array.getClass().isArray())
    {
      final int length = Array.getLength(array);
      for (int i = 0; i < length; i += 1)
      {
        this.put(JSONObject.wrap(Array.get(array, i)));
      }
    }
    else
    {
      throw new JSONException("JSONArray initial value should be a string or collection or array.");
    }
  }

  /**
   * Make a string from the contents of this JSONArray. The
   * <code>separator</code> string is inserted between each element.
   * Warning: This method assumes that the data structure is acyclical.
   *
   * @param separator
   *        A string that will be inserted between the elements.
   * @return a string.
   * @throws JSONException
   *         If the array contains an invalid number.
   */
  public String join(final String separator)
    throws JSONException
  {
    final int len = length();
    final StringBuffer sb = new StringBuffer();

    for (int i = 0; i < len; i += 1)
    {
      if (i > 0)
      {
        sb.append(separator);
      }
      sb.append(JSONObject.valueToString(myArrayList.get(i)));
    }
    return sb.toString();
  }

  /**
   * Get the number of elements in the JSONArray, included nulls.
   *
   * @return The length (or size).
   */
  public int length()
  {
    return myArrayList.size();
  }

  /**
   * Append a boolean value. This increases the array's length by one.
   *
   * @param value
   *        A boolean value.
   * @return this.
   */
  public JSONArray put(final boolean value)
  {
    put(value? Boolean.TRUE: Boolean.FALSE);
    return this;
  }

  /**
   * Put a value in the JSONArray, where the value will be a JSONArray
   * which is produced from a Collection.
   *
   * @param value
   *        A Collection value.
   * @return this.
   */
  public JSONArray put(final Collection value)
  {
    put(new JSONArray(value));
    return this;
  }

  /**
   * Append a double value. This increases the array's length by one.
   *
   * @param value
   *        A double value.
   * @throws JSONException
   *         if the value is not finite.
   * @return this.
   */
  public JSONArray put(final double value)
    throws JSONException
  {
    final Double d = new Double(value);
    JSONObject.testValidity(d);
    put(d);
    return this;
  }

  /**
   * Append an int value. This increases the array's length by one.
   *
   * @param value
   *        An int value.
   * @return this.
   */
  public JSONArray put(final int value)
  {
    put(new Integer(value));
    return this;
  }

  /**
   * Put or replace a boolean value in the JSONArray. If the index is
   * greater than the length of the JSONArray, then null elements will
   * be added as necessary to pad it out.
   *
   * @param index
   *        The subscript.
   * @param value
   *        A boolean value.
   * @return this.
   * @throws JSONException
   *         If the index is negative.
   */
  public JSONArray put(final int index, final boolean value)
    throws JSONException
  {
    put(index, value? Boolean.TRUE: Boolean.FALSE);
    return this;
  }

  /**
   * Put a value in the JSONArray, where the value will be a JSONArray
   * which is produced from a Collection.
   *
   * @param index
   *        The subscript.
   * @param value
   *        A Collection value.
   * @return this.
   * @throws JSONException
   *         If the index is negative or if the value is not finite.
   */
  public JSONArray put(final int index, final Collection value)
    throws JSONException
  {
    put(index, new JSONArray(value));
    return this;
  }

  /**
   * Put or replace a double value. If the index is greater than the
   * length of the JSONArray, then null elements will be added as
   * necessary to pad it out.
   *
   * @param index
   *        The subscript.
   * @param value
   *        A double value.
   * @return this.
   * @throws JSONException
   *         If the index is negative or if the value is not finite.
   */
  public JSONArray put(final int index, final double value)
    throws JSONException
  {
    put(index, new Double(value));
    return this;
  }

  /**
   * Put or replace an int value. If the index is greater than the
   * length of the JSONArray, then null elements will be added as
   * necessary to pad it out.
   *
   * @param index
   *        The subscript.
   * @param value
   *        An int value.
   * @return this.
   * @throws JSONException
   *         If the index is negative.
   */
  public JSONArray put(final int index, final int value)
    throws JSONException
  {
    put(index, new Integer(value));
    return this;
  }

  /**
   * Put or replace a long value. If the index is greater than the
   * length of the JSONArray, then null elements will be added as
   * necessary to pad it out.
   *
   * @param index
   *        The subscript.
   * @param value
   *        A long value.
   * @return this.
   * @throws JSONException
   *         If the index is negative.
   */
  public JSONArray put(final int index, final long value)
    throws JSONException
  {
    put(index, new Long(value));
    return this;
  }

  /**
   * Put a value in the JSONArray, where the value will be a JSONObject
   * that is produced from a Map.
   *
   * @param index
   *        The subscript.
   * @param value
   *        The Map value.
   * @return this.
   * @throws JSONException
   *         If the index is negative or if the the value is an invalid
   *         number.
   */
  public JSONArray put(final int index, final Map value)
    throws JSONException
  {
    put(index, new JSONObject(value));
    return this;
  }

  /**
   * Put or replace an object value in the JSONArray. If the index is
   * greater than the length of the JSONArray, then null elements will
   * be added as necessary to pad it out.
   *
   * @param index
   *        The subscript.
   * @param value
   *        The value to put into the array. The value should be a
   *        Boolean, Double, Integer, JSONArray, JSONObject, Long, or
   *        String, or the JSONObject.NULL object.
   * @return this.
   * @throws JSONException
   *         If the index is negative or if the the value is an invalid
   *         number.
   */
  public JSONArray put(final int index, final Object value)
    throws JSONException
  {
    JSONObject.testValidity(value);
    if (index < 0)
    {
      throw new JSONException("JSONArray[" + index + "] not found.");
    }
    if (index < length())
    {
      myArrayList.set(index, value);
    }
    else
    {
      while (index != length())
      {
        put(JSONObject.NULL);
      }
      put(value);
    }
    return this;
  }

  /**
   * Append an long value. This increases the array's length by one.
   *
   * @param value
   *        A long value.
   * @return this.
   */
  public JSONArray put(final long value)
  {
    put(new Long(value));
    return this;
  }

  /**
   * Put a value in the JSONArray, where the value will be a JSONObject
   * which is produced from a Map.
   *
   * @param value
   *        A Map value.
   * @return this.
   */
  public JSONArray put(final Map value)
  {
    put(new JSONObject(value));
    return this;
  }

  /**
   * Append an object value. This increases the array's length by one.
   *
   * @param value
   *        An object value. The value should be a Boolean, Double,
   *        Integer, JSONArray, JSONObject, Long, or String, or the
   *        JSONObject.NULL object.
   * @return this.
   */
  public JSONArray put(final Object value)
  {
    myArrayList.add(value);
    return this;
  }

  /**
   * Make a JSON text of this JSONArray. For compactness, no unnecessary
   * whitespace is added. If it is not possible to produce a
   * syntactically correct JSON text then null will be returned instead.
   * This could occur if the array contains an invalid number.
   * <p>
   * Warning: This method assumes that the data structure is acyclical.
   *
   * @return a printable, displayable, transmittable representation of
   *         the array.
   */
  @Override
  public String toString()
  {
    try
    {
      return '[' + join(",") + ']';
    }
    catch (final Exception e)
    {
      return null;
    }
  }

  /**
   * Make a prettyprinted JSON text of this JSONArray. Warning: This
   * method assumes that the data structure is acyclical.
   *
   * @param indentFactor
   *        The number of spaces to add to each level of indentation.
   * @return a printable, displayable, transmittable representation of
   *         the object, beginning with <code>[</code>&nbsp;<small>(left
   *         bracket)</small> and ending with <code>]</code> &nbsp;
   *         <small>(right bracket)</small>.
   * @throws JSONException
   */
  public String toString(final int indentFactor)
    throws JSONException
  {
    return toString(indentFactor, 0);
  }

  /**
   * Write the contents of the JSONArray as JSON text to a writer. For
   * compactness, no whitespace is added.
   * <p>
   * Warning: This method assumes that the data structure is acyclical.
   *
   * @return The writer.
   * @throws JSONException
   */
  public Writer write(final Writer writer)
    throws JSONException
  {
    try
    {
      boolean b = false;
      final int len = length();

      writer.write('[');

      for (int i = 0; i < len; i += 1)
      {
        if (b)
        {
          writer.write(',');
        }
        final Object v = myArrayList.get(i);
        if (v instanceof JSONObject)
        {
          ((JSONObject) v).write(writer);
        }
        else if (v instanceof JSONArray)
        {
          ((JSONArray) v).write(writer);
        }
        else
        {
          writer.write(JSONObject.valueToString(v));
        }
        b = true;
      }
      writer.write(']');
      return writer;
    }
    catch (final IOException e)
    {
      throw new JSONException(e);
    }
  }

  /**
   * Make a prettyprinted JSON text of this JSONArray. Warning: This
   * method assumes that the data structure is acyclical.
   *
   * @param indentFactor
   *        The number of spaces to add to each level of indentation.
   * @param indent
   *        The indention of the top level.
   * @return a printable, displayable, transmittable representation of
   *         the array.
   * @throws JSONException
   */
  String toString(final int indentFactor, final int indent)
    throws JSONException
  {
    final int len = length();
    if (len == 0)
    {
      return "[]";
    }
    int i;
    final StringBuffer sb = new StringBuffer("[");
    if (len == 1)
    {
      sb.append(JSONObject
        .valueToString(myArrayList.get(0), indentFactor, indent));
    }
    else
    {
      final int newindent = indent + indentFactor;
      sb.append('\n');
      for (i = 0; i < len; i += 1)
      {
        if (i > 0)
        {
          sb.append(",\n");
        }
        for (int j = 0; j < newindent; j += 1)
        {
          sb.append(' ');
        }
        sb.append(JSONObject
          .valueToString(myArrayList.get(i), indentFactor, newindent));
      }
      sb.append('\n');
      for (i = 0; i < indent; i += 1)
      {
        sb.append(' ');
      }
    }
    sb.append(']');
    return sb.toString();
  }

  /**
   * Make a prettyprinted JSON text of this JSONArray. Warning: This
   * method assumes that the data structure is acyclical.
   *
   * @param indentFactor
   *        The number of spaces to add to each level of indentation.
   * @param indent
   *        The indention of the top level.
   * @return a printable, displayable, transmittable representation of
   *         the array.
   * @throws JSONException
   */
  void write(final PrintWriter writer, final int indentFactor, final int indent)
    throws JSONException
  {
    final int len = length();
    if (len == 0)
    {
      writer.write("[]");
      return;
    }
    int i;
    writer.write("[");
    if (len == 1)
    {
      writer.write(JSONObject
        .valueToString(myArrayList.get(0), indentFactor, indent));
    }
    else
    {
      final int newindent = indent + indentFactor;
      writer.println();
      for (i = 0; i < len; i += 1)
      {
        if (i > 0)
        {
          writer.println(",");
        }
        for (int j = 0; j < newindent; j += 1)
        {
          writer.write(' ');
        }
        writer.print(JSONObject
          .valueToString(myArrayList.get(i), indentFactor, newindent));
      }
      writer.println();
      for (i = 0; i < indent; i += 1)
      {
        writer.print(' ');
      }
    }
    writer.print(']');
  }

}
