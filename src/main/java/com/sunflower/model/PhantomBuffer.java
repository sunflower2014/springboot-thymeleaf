package com.sunflower.model;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

public class PhantomBuffer {
	private byte[] data = new byte[0];
	private ReferenceQueue<byte[]> queue = new ReferenceQueue<byte[]>();
	private PhantomReference<byte[]> ref = new PhantomReference<byte[]>(data, queue);
	
	public byte[] get(int size) {
		if (size <= 0) {
			throw new IllegalArgumentException("Wrong buffer size");
		}
		if (data.length < size) {
			data = null;
			System.gc();
			
			try {
				queue.remove();
				ref.clear();
				ref = null;
				data = new byte[size];
				ref = new PhantomReference<byte[]>(data, queue);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

}
