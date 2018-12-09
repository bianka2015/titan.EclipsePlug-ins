/******************************************************************************
 * Copyright (c) 2000-2018 Ericsson Telecom AB
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.html
 ******************************************************************************/
package org.eclipse.titan.runtime.core;

import java.text.MessageFormat;

import org.eclipse.titan.runtime.core.Param_Types.Module_Parameter;
import org.eclipse.titan.runtime.core.Param_Types.Module_Parameter.basic_check_bits_t;
import org.eclipse.titan.runtime.core.Param_Types.Module_Parameter.type_t;

/**
 * @author Farkas Izabella Ingrid
 */
public class TitanValue_Array<T extends Base_Type> extends Base_Type {

	Base_Type[] array_elements;

	public Class<T> clazz;

	int array_size;
	int indexOffset;

	// only package visible
/*	TitanValueArray(final Class<T> clazz) {
		this.clazz = clazz;
		array_elements = new ArrayList<T>();
	}*/

	public TitanValue_Array(final TitanValue_Array<T> otherValue) {
		clazz = otherValue.clazz;
		array_size = otherValue.array_size;
		indexOffset = otherValue.indexOffset;
		array_elements = new Base_Type[array_size];

		for (int i = 0; i < array_size; ++i) {
			try {
				final T helper = clazz.newInstance();
				helper.operator_assign(otherValue.array_elements[i]);
				array_elements[i] = helper;
			} catch (InstantiationException e) {
				throw new TtcnError(MessageFormat.format("Internal error: class `{0}'' could not be instantiated ({1}).", clazz, e));
			} catch (IllegalAccessException e) {
				throw new TtcnError(MessageFormat.format("Internal error: class `{0}'' could not be instantiated ({1}).", clazz, e));
			}
		}
	}

	public TitanValue_Array(final Class<T> clazz, final int size, final int offset) {
		this.clazz = clazz;
		indexOffset = offset;

		array_size = size;
		array_elements = new Base_Type[size];
		for (int i = 0; i < size; ++i) {
			try {
				final T emply = clazz.newInstance();
				array_elements[i] = emply;
			} catch (InstantiationException e) {
				throw new TtcnError(MessageFormat.format("Internal error: class `{0}'' could not be instantiated ({1}).", clazz, e));
			} catch (IllegalAccessException e) {
				throw new TtcnError(MessageFormat.format("Internal error: class `{0}'' could not be instantiated ({1}).", clazz, e));
			}
		}
	}

	//FIXME: implement
/*	public void set_size(final int length) {
		for (int i = array_size; i < length; ++i) {
			try {
				final T emply = clazz.newInstance();
				array_elements[i] = emply;
			} catch (InstantiationException e) {
				throw new TtcnError(MessageFormat.format("Internal error: class `{0}'' could not be instantiated ({1}).", clazz, e));
			} catch (IllegalAccessException e) {
				throw new TtcnError(MessageFormat.format("Internal error: class `{0}'' could not be instantiated ({1}).", clazz, e));
			}
		}
		array_size = length;
	}*/

	public int get_offset() {
		return indexOffset;
	}

	public void set_offset(final int offset) {
		indexOffset = offset;
	}

	@Override
	public void set_implicit_omit() {
		for (int i = 0; i < array_size; ++i) {
			array_elements[i].set_implicit_omit();
		}
	}

	@Override
	public boolean is_present() {
		return is_bound();
	}

	@Override
	public boolean is_bound() {
		for (int i = 0; i < array_size; ++i) {
			if (array_elements[i].is_bound()) {
				return true;
			}
		}

		return false;
	}

	//FIXME: originally array_elements.get(i).clean_up()
	@Override
	public void clean_up() {
		//array_elements.clear();
		array_elements = null;
	}

	@Override
	public boolean is_value() {
		for (int i = 0; i < array_size; ++i) {
			if (!array_elements[i].is_value()) {
				return false;
			}
		}

		return true;
	}

	public TitanInteger lengthof() {
		for (int i = array_size - 1; i >= 0; --i) {
			if (array_elements[i].is_bound()) {
				return new TitanInteger(i + 1);
			}
		}

		return new TitanInteger(0);
	}

	// TODO: void set_param(Module_Param& param);

	// originally not implemented operator=
	@SuppressWarnings("unchecked")
	@Override
	public TitanValue_Array<T> operator_assign(final Base_Type otherValue) {
		if (otherValue instanceof TitanValue_Array<?>) {
			final TitanValue_Array<T> arrayOther = (TitanValue_Array<T>)otherValue;
			return operator_assign(arrayOther);
		} else {
			try {
				array_size = 1;
				array_elements = new Base_Type[1];
				final T value = clazz.newInstance();
				value.operator_assign(otherValue);
				array_elements[0] = value;
			} catch (InstantiationException e) {
				throw new TtcnError(MessageFormat.format("Internal error: class `{0}'' could not be instantiated ({1}).", clazz, e));
			} catch (IllegalAccessException e) {
				throw new TtcnError(MessageFormat.format("Internal error: class `{0}'' could not be instantiated ({1}).", clazz, e));
			}

			return this;
		}
	}

	public TitanValue_Array<T> operator_assign(final TitanValue_Array<T> otherValue) {
		clean_up();
		array_size = otherValue.array_size;
		indexOffset = otherValue.indexOffset;
		array_elements = new Base_Type[array_size];

		for (int i = 0; i < otherValue.array_size; ++i) {
			try {
				final T helper = clazz.newInstance();
				helper.operator_assign(otherValue.array_element(i));
				array_elements[i] = helper;
			} catch (InstantiationException e) {
				throw new TtcnError(MessageFormat.format("Internal error: class `{0}'' could not be instantiated ({1}).", clazz, e));
			} catch (IllegalAccessException e) {
				throw new TtcnError(MessageFormat.format("Internal error: class `{0}'' could not be instantiated ({1}).", clazz, e));
			}
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean operator_equals(final Base_Type otherValue) {
		if (otherValue instanceof TitanValue_Array<?>) {
			final TitanValue_Array<T> arrayOther = (TitanValue_Array<T>)otherValue;
			return operator_equals(arrayOther);
		} else {
			if (array_size == 1) {
				return array_elements[0].operator_equals(otherValue);
			}
		}

		throw new TtcnError(MessageFormat.format("Internal Error: value `{0}'' can not be cast to array value", otherValue));
	}

	/**
	 * Checks if the current value is equivalent to the provided one.
	 *
	 * operator== in the core
	 *
	 * @param otherValue
	 *                the other value to check against.
	 * @return {@code true} if the values are equivalent.
	 */
	public boolean operator_equals(final TitanValue_Array<T> otherValue) {
		if (array_size != otherValue.array_size) {
			return false;
		}

		for (int i = 0; i < array_size; ++i) {
			if (! array_elements[i].operator_equals(otherValue.array_elements[i])) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks if the current value is not equivalent to the provided one.
	 *
	 * operator!= in the core
	 *
	 * @param otherValue
	 *                the other value to check against.
	 * @return {@code true} if the values are not equivalent.
	 */
	public boolean operator_not_equals(final Base_Type otherValue) {
		return !operator_equals(otherValue);
	}

	/**
	 * Checks if the current value is not equivalent to the provided one.
	 *
	 * operator!= in the core
	 *
	 * @param otherValue
	 *                the other value to check against.
	 * @return {@code true} if the values are not equivalent.
	 */
	public boolean operator_not_equals(final TitanValue_Array<T> otherValue) {
		return !operator_equals(otherValue);
	}

	// originally  operator<<=
	public TitanValue_Array<T> rotate_left(int rotateCount) {
		//new TitanValueArray<T>((TitanValueArray<T>).getClass());
		if (array_size == 0) {
			return this;
		}
		if (rotateCount >= 0) {
			rotateCount = rotateCount % array_size;
			if (rotateCount == 0) {
				return this;
			}

			final TitanValue_Array<T> result = new TitanValue_Array<T>(clazz, array_size, indexOffset);
//			result.array_size = array_size;
//			result.indexOffset = indexOffset;
			if (rotateCount > array_size) {
				rotateCount = array_size;
			}
			for (int i = 0; i < array_size - rotateCount; i++) {
				result.array_elements[i] = array_elements[i + rotateCount];
			}
			for (int i = array_size - rotateCount; i < array_size; i++) {
				result.array_elements[i] = array_elements[i + rotateCount - array_size];
			}
			return result;
		} else {
			return rotate_left(-rotateCount);
		}
	}

	//originally  operator<<=
	public TitanValue_Array<T> rotate_left(final TitanInteger rotateCount) {
		rotateCount.must_bound("Unbound integer operand of rotate left operator.");

		return rotate_left(rotateCount.getInt());
	}

	//originally  operator>>=
	public TitanValue_Array<T> rotate_right(int rotateCount) {
		if (array_size == 0) {
			return this;
		}
		if (rotateCount >= 0) {
			rotateCount = rotateCount % array_size;
			if (rotateCount == 0) {
				return this;
			}

			final TitanValue_Array<T> result = new TitanValue_Array<T>(clazz, array_size, indexOffset);
//			result.array_size = array_size;
//			result.indexOffset = indexOffset;
			if (rotateCount > array_size) {
				rotateCount = array_size;
			}
			for (int i = 0; i < rotateCount; i++) {
				result.array_elements[i] = array_elements[i - rotateCount + array_size];
			}
			for (int i = rotateCount; i < array_size; i++) {
				result.array_elements[i] = array_elements[i - rotateCount];
			}
			return result;
		} else {
			return rotate_left(-rotateCount);
		}
	}

	//originally  operator>>=
	public TitanValue_Array<T> rotate_right(final TitanInteger rotateCount) {
		rotateCount.must_bound("Unbound integer operand of rotate right operator.");

		return rotate_right(rotateCount.getInt());
	}

	// originally T& operator[](int)
	@SuppressWarnings("unchecked")
	public T get_at(final int index) {
		return (T)array_elements[get_array_index(index, array_size, indexOffset)];
	}

	//originally T& operator[](const INTEGER)
	@SuppressWarnings("unchecked")
	public T get_at(final TitanInteger index) {
		return (T)array_elements[get_array_index(index, array_size, indexOffset)];
	}
	//const originally T& operator[](int)
	@SuppressWarnings("unchecked")
	public T constGet_at(final int index) {
		return (T)array_elements[get_array_index(index, array_size, indexOffset)];
	}

	// const // originally T& operator[](const INTEGER)
	@SuppressWarnings("unchecked")
	public T constGet_at(final TitanInteger index) {
		return (T)array_elements[get_array_index(index, array_size, indexOffset)];
	}

	@SuppressWarnings("unchecked")
	public T array_element(final int index) {
		return (T)array_elements[index];
	}

	@SuppressWarnings("unchecked")
	public T array_element(final TitanInteger index) {
		index.must_bound("Accessing an element of an array using an unbound index.");

		return (T)array_elements[index.getInt()];
	}

	// originally n_elem()
	public int n_elem() {
		return array_size;
	}

	/**
	 * Returns the number of elements, that is, the largest used index plus
	 * one and zero for the empty value.
	 *
	 * size_of in the core
	 *
	 * @return the number of elements.
	 * */
	public TitanInteger size_of() {
		return new TitanInteger(array_size);
	}

	/** 
	 * Do not use this function!<br>
	 * It is provided by Java and currently used for debugging.
	 * But it is not part of the intentionally provided interface,
	 *   and so can be changed without notice. 
	 * <p>
	 * JAVA DESCRIPTION:
	 * <p>
	 * {@inheritDoc}
	 *  */
	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder("{");
		for (int i = 0; i < array_size - 1; ++i) {
			str.append(array_elements[i].toString());
			str.append(" , ");
		}
		if (array_size > 0) {
			str.append(array_elements[array_size - 1].toString());
		}
		str.append('}');
		return str.toString();
	}

	//static method

	public static int get_array_index(final int index, final int arraySize, final int indexofset) {
		if (arraySize < 0) {
			throw new TtcnError("Invalid array size");
		}
		if (index < indexofset) {
			throw new TtcnError("Index underflow when accessing an element of an array. " + "The index value should be between "
					+ indexofset + " and " + (indexofset + arraySize - 1) + " instead of " + index + ".");
		}

		final int result = index - indexofset;
		if (result >= arraySize) {
			throw new TtcnError("Index overflow when accessing an element of an array. " + "The index value should be between "
					+ indexofset + " and " + (indexofset + arraySize - 1) + " instead of " + index + ".");
		}

		return result;
	}

	public static int get_array_index(final TitanInteger index, final int arraySize, final int indexofset) {
		index.must_bound("Accessing an element of an array using an unbound index.");

		return get_array_index(index.getInt(), arraySize, indexofset);
	}

	@Override
	public void log() {
		TTCN_Logger.log_event_str("{ ");
		for (int elem_count = 0; elem_count < array_size; elem_count++) {
			if (elem_count > 0) {
				TTCN_Logger.log_event_str(", ");
			}
			array_elements[elem_count].log();
		}
		TTCN_Logger.log_event_str(" }");
	}

	@Override
	public void set_param(final Module_Parameter param) {
		param.basic_check(basic_check_bits_t.BC_VALUE.getValue(), "array value");
		switch (param.get_type()) {
		case MP_Value_List:
			if (param.get_size() != array_size) {
				param.error("The array value has incorrect number of elements: %lu was expected instead of %lu.", param.get_size(), array_size);
			}
			for (int i = 0; i < param.get_size(); i++) {
				final Module_Parameter curr = param.get_elem(i);
				if (curr.get_type() != type_t.MP_NotUsed) {
					array_elements[i].set_param(curr);
				}
			}
			break;
		case MP_Indexed_List:
			for (int i = 0; i < param.get_size(); i++) {
				final Module_Parameter curr = param.get_elem(i);
				array_elements[curr.get_id().get_index()].set_param(curr);
			}
			break;
		default:
			param.type_error("array value");
		}
	}

	@Override
	/** {@inheritDoc} */
	public void encode_text(final Text_Buf text_buf) {
		for (int i = 0; i < array_size; i++) {
			array_elements[i].encode_text(text_buf);
		}
	}

	@Override
	/** {@inheritDoc} */
	public void decode_text(final Text_Buf text_buf) {
		for (int i = 0; i < array_size; i++) {
			array_elements[i].decode_text(text_buf);
		}
	}

	@Override
	/** {@inheritDoc} */
	public void encode(final TTCN_Typedescriptor p_td, final TTCN_Buffer p_buf, final TTCN_EncDec.coding_type p_coding, final int flavour) {
		switch (p_coding) {
		case CT_JSON:
			//TODO: implement JSON support
			break;
		default:
			throw new TtcnError(MessageFormat.format("Unknown coding method requested to encode type {0}", p_td.name));
		}
	}
	
	@Override
	/** {@inheritDoc} */
	public void decode(final TTCN_Typedescriptor p_td, final TTCN_Buffer p_buf, final TTCN_EncDec.coding_type p_coding, final int flavour) {
		switch (p_coding) {
		case CT_JSON:
			//TODO: implement JSON support
			break;
		default:
			throw new TtcnError(MessageFormat.format("Unknown coding method requested to decode type {0}", p_td.name));
		}
	}

	public TitanAlt_Status done(final TitanVerdictType value_redirect, final Index_Redirect index_redirect) {
		if (index_redirect != null) {
			index_redirect.incrPos();
		}

		TitanAlt_Status result = TitanAlt_Status.ALT_NO;
		for (int i = 0; i < array_size; i++) {
			final TitanAlt_Status returnValue = ((TitanComponent)array_elements[i]).done(value_redirect, index_redirect);
			if (returnValue == TitanAlt_Status.ALT_YES) {
				if (index_redirect != null) {
					index_redirect.addIndex(i + indexOffset);
				}

				result = returnValue;
				break;
			} else if (returnValue == TitanAlt_Status.ALT_REPEAT ||
					(returnValue == TitanAlt_Status.ALT_MAYBE && result == TitanAlt_Status.ALT_NO)) {
				result = returnValue;
			}
		}

		if (index_redirect != null) {
			index_redirect.decrPos();
		}

		return result;
	}

	public TitanAlt_Status killed(final Index_Redirect index_redirect) {
		if (index_redirect != null) {
			index_redirect.incrPos();
		}

		TitanAlt_Status result = TitanAlt_Status.ALT_NO;
		for (int i = 0; i < array_size; i++) {
			final TitanAlt_Status returnValue = ((TitanComponent)array_elements[i]).killed(index_redirect);
			if (returnValue == TitanAlt_Status.ALT_YES) {
				if (index_redirect != null) {
					index_redirect.addIndex(i + indexOffset);
				}

				result = returnValue;
				break;
			}
		}

		if (index_redirect != null) {
			index_redirect.decrPos();
		}

		return result;
	}

	public boolean running(final Index_Redirect index_redirect) {
		if (index_redirect != null) {
			index_redirect.incrPos();
		}

		boolean returnValue = false;
		for (int i = 0; i < array_size; i++) {
			returnValue = ((TitanComponent)array_elements[i]).alive(index_redirect);
			if (returnValue) {
				if (index_redirect != null) {
					index_redirect.addIndex(i + indexOffset);
				}
				break;
			}
		}

		if (index_redirect != null) {
			index_redirect.decrPos();
		}

		return returnValue;
	}

	public boolean alive(final Index_Redirect index_redirect) {
		if (index_redirect != null) {
			index_redirect.incrPos();
		}

		boolean returnValue = false;
		for (int i = 0; i < array_size; i++) {
			returnValue = ((TitanComponent)array_elements[i]).alive(index_redirect);
			if (returnValue) {
				if (index_redirect != null) {
					index_redirect.addIndex(i + indexOffset);
				}
				break;
			}
		}

		if (index_redirect != null) {
			index_redirect.decrPos();
		}

		return returnValue;
	}
}