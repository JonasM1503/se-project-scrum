/*
 * Copyright (c) 2000 Anton Marsden
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package corewars.jmars.marsVM;

public class ModularValue {

    protected int value;

    static protected final ArithmeticException aException
	= new ArithmeticException("Invalid mod value");

    public ModularValue() {
	value = 0;
    }

    public ModularValue(long val, ModularValue mod) {
	set(val, mod);
    }

    public ModularValue(int val) {
	if(val < 0) throw new ArithmeticException("Invalid value");
	value = val;
    }

    public ModularValue(ModularValue val) {
	copy(val);
    }

    public int get() {
	return value;
    }

    public int get(ModularValue mod) {
	return (value % mod.get());
    }

    public void set(long val, ModularValue mod) {
	if(mod.get() == 0) throw aException;
	if(val >= 0) {
	    value = ((int) (val % mod.get()));
	} else {
	    value = mod.get() - ((int) ((-val) % mod.get()));
	}
    }

    public void copy(ModularValue mv) {
	value = mv.get();
    }

    public void set(ModularValue mv, ModularValue mod) {
	value = mv.get(mod);
    }

    public void add(ModularValue mv, ModularValue mod) {
	if(get() >= mod.get() || mv.get() >= mod.get() || mod.get() == 0) throw aException;
	value += mv.get();
	if(value >= mod.get()) value -= mod.get();
    }

    public void subtract(ModularValue mv, ModularValue mod) {
	if(get() >= mod.get() || mv.get() >= mod.get() || mod.get() == 0) throw aException;
	value -= mv.get();
	if(value < 0) value += mod.get();
    }

    public void multiply(ModularValue mv, ModularValue mod) {
	if(get() >= mod.get() || mv.get() >= mod.get() || mod.get() == 0) throw aException;
	value = (value * mv.get()) % mod.get();
    }

    public void divide(ModularValue mv, ModularValue mod) {
	if(get() >= mod.get() || mv.get() >= mod.get() || mv.get() == 0) throw aException;
	value /= mv.get();
    }

    public void modulus(ModularValue mv, ModularValue mod) {
	if(get() >= mod.get() || mv.get() >= mod.get() || mv.get() == 0) throw aException;
	value %= mv.get();
    }

    public void increment(ModularValue mod) {
	if(get() >= mod.get() || mod.get() == 0) throw aException;
	value++;
	if(value >= mod.get()) value -= mod.get();
    }

    public void decrement(ModularValue mod) {
	if(get() >= mod.get() || mod.get() == 0) throw aException;
	value--;
	if(value < 0) value += mod.get();
    }

    public int compare(ModularValue val) {
	return (val.get() - value);
    }

    public String toString() {
	return new Integer(value).toString();
    }
}
