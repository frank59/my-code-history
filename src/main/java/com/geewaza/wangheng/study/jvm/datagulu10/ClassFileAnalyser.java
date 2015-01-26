package com.geewaza.wangheng.study.jvm.datagulu10;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

public class ClassFileAnalyser {

	private static String classFile = "D:\\opt\\HelloWorld.class";

	public static void main(String[] args) throws IOException {
		test001();
	}
	
	public static boolean isClassFile(RandomAccessFile raf) throws IOException {
		if (getU4Int(raf, 0x0000) != 0xCAFEBABE) {
			return false;
		}
		return true;
	}

	private static void test001() throws IOException {
		RandomAccessFile fileInputStream = new RandomAccessFile(classFile, "r");

		try {
			List<JSONObject> constantPoolList = new ArrayList<JSONObject>();
			int constantPoolSize = analyseConstantPool(constantPoolList, fileInputStream);
			int classSize = getSuperClassInfo(fileInputStream, constantPoolList, 0x0009 + constantPoolSize);
			int fieldsSize = getFieldsInfo(fileInputStream, constantPoolList, 0x0009 + constantPoolSize + classSize);
			int methodsSize = getMethodInfo(fileInputStream, constantPoolList, 0x0009 + constantPoolSize + classSize + fieldsSize);
		} finally {
			fileInputStream.close();
		}

	}
	
	/**
	 * 分析方法信息
	 * @param raf
	 * @param constantPoolList
	 * @param off
	 * @return
	 * @throws IOException
	 */
	private static int getMethodInfo(RandomAccessFile raf,
			List<JSONObject> constantPoolList, int off) throws IOException {
		int methodCount = getU2Int(raf, off);
		if (methodCount != 0) {
			long seek = off + 2;
			StringBuilder methodInfo = new StringBuilder();
			for (int i = 0; i < methodCount; i++) {
				methodInfo.append(getMethodAccessFlags(getByteArray(raf, seek, 2))).append(" ");
				methodInfo.append(getConstantValue(constantPoolList, getU2Int(raf, seek + 4))).append(" ");
				methodInfo.append(getConstantValue(constantPoolList, getU2Int(raf, seek + 2))).append("\n");
				long attrSize = 0;
				int attrCount = getU2Int(raf, seek + 6);
				if (attrCount != 0) {
					//分析attribute
					for(int at = 0; at < attrCount; at++) {
						long size = getU4Int(raf, seek + attrSize + 10);
						attrSize += 4 + size + 2;
					}
				}
				seek += 8 + attrSize;
			}
			System.out.println(methodInfo.toString());
		}
		return (int) (raf.getFilePointer() - off);
	}

	/**
	 * 分析字段信息
	 * @param raf
	 * @param constantPoolList
	 * @param off
	 * @return
	 * @throws IOException
	 */
	private static int getFieldsInfo(RandomAccessFile raf,
			List<JSONObject> constantPoolList, int off) throws IOException {
		int fieldCount = getU2Int(raf, off);
		if (fieldCount != 0) {
			int seek = off + 2;
			StringBuilder fieldInfo = new StringBuilder();
			for (int i = 0; i < fieldCount; i++) {
				fieldInfo.append(getAttributesAccessFlags(getByteArray(raf, seek, 2))).append(" ");
				fieldInfo.append(getConstantValue(constantPoolList, getU2Int(raf, seek + 2))).append(" ");
				fieldInfo.append(getConstantValue(constantPoolList, getU2Int(raf, seek + 4))).append("\n");
				seek += 8;
			}
			System.out.println(fieldInfo.toString());
		}
		return 2 + fieldCount * 8;
	}

	/**
	 * 分析类信息和超类接口信息
	 * @param raf
	 * @param constantPoolList
	 * @param off
	 * @return
	 * @throws IOException
	 */
	public static int getSuperClassInfo(RandomAccessFile raf, List<JSONObject> constantPoolList, long off) throws IOException{
		long seek = off + 2;
		int classIndex = getU2Int(raf, seek);
		System.out.println("类名：" + getClassAccessFlags(getByteArray(raf, off, 2)) + getConstantValue(constantPoolList, classIndex).replaceAll("/", "."));
		int superClassIndex = getU2Int(raf, seek + 2);
		System.out.println("超类名：" + getConstantValue(constantPoolList, superClassIndex).replaceAll("/", "."));
		int interfaceCount = getU2Int(raf, seek + 4);
		if (interfaceCount != 0) {
			StringBuilder interfaceInfo = new StringBuilder();
			for (int i = 0; i < interfaceCount; i++) {
				int interfaceIndex = getU2Int(raf, seek + 6 + (i * 2));
				interfaceInfo.append(getConstantValue(constantPoolList, interfaceIndex).replaceAll("/", ".")).append(" ");
			}
			System.out.println("实现接口：" + interfaceInfo);
		}
		return 8 + interfaceCount * 2;
	}

	public static String getString(RandomAccessFile raf, long pos, int length)
			throws IOException {
		byte[] byteArray = new byte[length];
		raf.seek(pos);
		raf.read(byteArray);
		String result = new String(byteArray, "utf-8");
		return result;
	}

	public static String byteToString(byte[] byteArray)
			throws UnsupportedEncodingException {
		return new String(byteArray, "utf-8");
	}

	public static int getU2Int(RandomAccessFile raf, long pos)
			throws IOException {
		byte[] byteArray = new byte[2];
		raf.seek(pos);
		raf.read(byteArray);
		int result = byteArray[1] & 0xFF | (byteArray[0] & 0xFF) << 8;
		return result;
	}

	public static int getU1Int(RandomAccessFile raf, long pos)
			throws IOException {
		byte[] byteArray = new byte[1];
		raf.seek(pos);
		raf.read(byteArray);
		int result = byteArray[0];
		return result;
	}

	public static int getU4Int(RandomAccessFile raf, long pos)
			throws IOException {
		byte[] byteArray = new byte[4];
		raf.seek(pos);
		raf.read(byteArray);

		int mask = 0xff;
		int temp = 0;
		int n = 0;
		for (int i = 0; i < 4; i++) {
			n <<= 8;
			temp = byteArray[i] & mask;
			n |= temp;
		}
		return n;
	}
	
	public static long getU8Long(RandomAccessFile raf, long pos)
			throws IOException {
		byte[] byteArray = new byte[8];
		raf.seek(pos);
		raf.read(byteArray);
		long longValue = (((long) byteArray[0] & 0xff) << 56)
				| (((long) byteArray[1] & 0xff) << 48)
				| (((long) byteArray[2] & 0xff) << 40)
				| (((long) byteArray[3] & 0xff) << 32)
				| (((long) byteArray[4] & 0xff) << 24)
				| (((long) byteArray[5] & 0xff) << 16)
				| (((long) byteArray[6] & 0xff) << 8)
				| (((long) byteArray[7] & 0xff) << 0);
		return longValue;
	}

	public static byte[] getByteArray(RandomAccessFile raf, long pos, int length)
			throws IOException {
		byte[] byteArray = new byte[length];
		raf.seek(pos);
		raf.read(byteArray);
		return byteArray;
	}

	public static int u2ByteToInt(byte[] byteArray) {
		return byteArray[1] & 0xFF | (byteArray[0] & 0xFF) << 8;
	}

	public static int u1ByteToInt(byte[] byteArray) {
		return byteArray[0];
	}

	public static int u4ByteToInt(byte[] byteArray) {
		return (byteArray[0] & 0xff) | ((byteArray[1] << 8) & 0xff00)
				| ((byteArray[2] << 24) >>> 8) | (byteArray[3] << 24);
	}

	public static long byteToLong(byte[] byteArray) throws IOException {
		long longValue = (((long) byteArray[0] & 0xff) << 56)
				| (((long) byteArray[1] & 0xff) << 48)
				| (((long) byteArray[2] & 0xff) << 40)
				| (((long) byteArray[3] & 0xff) << 32)
				| (((long) byteArray[4] & 0xff) << 24)
				| (((long) byteArray[5] & 0xff) << 16)
				| (((long) byteArray[6] & 0xff) << 8)
				| (((long) byteArray[7] & 0xff) << 0);
		return longValue;
	}

	/**
	 * 分析常量池
	 * @param constantPoolList
	 * @param raf
	 * @return
	 * @throws IOException
	 */
	public static int analyseConstantPool(List<JSONObject> constantPoolList, RandomAccessFile raf)
			throws IOException {
		int constantPoolSize = 0;
		int constantCount = getU2Int(raf, 0x0008) - 1;
		for (int seek = 0x000a; constantPoolList.size() < constantCount;) {
			int tag = getU1Int(raf, seek);
			switch (tag) {
			case 0x01: {
				JSONObject constantItem = new JSONObject();
				constantItem.put("tag", tag);
				int length = getU2Int(raf, seek + 1);
				String value = getString(raf, seek + 3, length);
				constantItem.put("length", length);
				constantItem.put("value", value);
				constantPoolList.add(constantItem);
				seek += (3 + length);
				break;
			}
			case 0x03: {
				JSONObject constantItem = new JSONObject();
				constantItem.put("tag", tag);
				int value = getU4Int(raf, seek + 1);
				constantItem.put("value", value);
				constantPoolList.add(constantItem);
				seek += 5;
				break;
			}
			case 0x04: {
				JSONObject constantItem = new JSONObject();
				constantItem.put("tag", tag);
				float value = Float.intBitsToFloat(getU4Int(raf, seek + 1));
				constantItem.put("value", value);
				constantPoolList.add(constantItem);
				seek += 5;
				break;
			}
			case 0x05: {
				JSONObject constantItem = new JSONObject();
				constantItem.put("tag", tag);
				long value = getU8Long(raf, seek + 1);
				constantItem.put("value", value);
				constantPoolList.add(constantItem);
				seek += 9;
				break;
			}
			case 0x06: {
				JSONObject constantItem = new JSONObject();
				constantItem.put("tag", tag);
				long longValue = getU8Long(raf, seek + 1);
				constantItem.put("value", Double.longBitsToDouble(longValue));
				constantPoolList.add(constantItem);
				seek += 9;
				break;
			}
			case 0x07: {
				JSONObject constantItem = new JSONObject();
				constantItem.put("tag", tag);
				int index = getU2Int(raf, seek + 1);
				constantItem.put("index", index);
				constantPoolList.add(constantItem);
				seek += 3;
				break;
			}
			case 0x08: {
				JSONObject constantItem = new JSONObject();
				constantItem.put("tag", tag);
				int index = getU2Int(raf, seek + 1);
				constantItem.put("index", index);
				constantPoolList.add(constantItem);
				seek += 3;
				break;
			}
			case 0x09: {
				JSONObject constantItem = new JSONObject();
				constantItem.put("tag", tag);
				int index1 = getU2Int(raf, seek + 1);
				int index2 = getU2Int(raf, seek + 3);
				constantItem.put("index1", index1);
				constantItem.put("index2", index2);
				constantPoolList.add(constantItem);
				seek += 5;
				break;
			}
			case 0x0a: {
				JSONObject constantItem = new JSONObject();
				constantItem.put("tag", tag);
				int index1 = getU2Int(raf, seek + 1);
				int index2 = getU2Int(raf, seek + 3);
				constantItem.put("index1", index1);
				constantItem.put("index2", index2);
				constantPoolList.add(constantItem);
				seek += 5;
				break;
			}
			case 0x0b: {
				JSONObject constantItem = new JSONObject();
				constantItem.put("tag", tag);
				int index1 = getU2Int(raf, seek + 1);
				int index2 = getU2Int(raf, seek + 3);
				constantItem.put("index1", index1);
				constantItem.put("index2", index2);
				constantPoolList.add(constantItem);
				seek += 5;
				break;
			}
			case 0x0c: {
				JSONObject constantItem = new JSONObject();
				constantItem.put("tag", tag);
				int index1 = getU2Int(raf, seek + 1);
				int index2 = getU2Int(raf, seek + 3);
				constantItem.put("index1", index1);
				constantItem.put("index2", index2);
				constantPoolList.add(constantItem);
				seek += 5;
				break;
			}
			case 0x0d: {
				JSONObject constantItem = new JSONObject();
				constantItem.put("tag", tag);
				int kind = getU1Int(raf, seek + 1);
				int index = getU2Int(raf, seek + 2);
				constantItem.put("kind", kind);
				constantItem.put("index", index);
				constantPoolList.add(constantItem);
				seek += 4;
				break;
			}
			case 0x10: {
				JSONObject constantItem = new JSONObject();
				constantItem.put("tag", tag);
				int index = getU2Int(raf, seek + 1);
				constantItem.put("index", index);
				constantPoolList.add(constantItem);
				seek += 3;
				break;
			}
			case 0x12: {
				JSONObject constantItem = new JSONObject();
				constantItem.put("tag", tag);
				int index1 = getU2Int(raf, seek + 1);
				int index2 = getU2Int(raf, seek + 3);
				constantItem.put("index1", index1);
				constantItem.put("index2", index2);
				constantPoolList.add(constantItem);
				seek += 5;
				break;
			}
			}
			constantPoolSize = seek - 0x0009;
		}
		return constantPoolSize;
	}
	
	/**
	 * 获取常量池中的值
	 * @param constantPoolList
	 * @param index
	 * @return
	 */
	public static String getConstantValue(List<JSONObject> constantPoolList, int index) {
		JSONObject constantValue = constantPoolList.get(index - 1);
		int tag = constantValue.getInt("tag");
		switch (tag) {
		case 0x01: {
			return constantValue.getString("value");
		}
		case 0x03: {
			return constantValue.getInt("value") + "";
		}
		case 0x04: {
			return constantValue.getDouble("value") + "";
		}
		case 0x05: {
			return constantValue.getLong("value") + "";
		}
		case 0x06: {
			return constantValue.getDouble("value") + "";
		}
		case 0x07: {
			int i = constantValue.getInt("index");
			return getConstantValue(constantPoolList, i);
		}
		case 0x08: {
			int i = constantValue.getInt("index");
			return getConstantValue(constantPoolList, i);
		}
		case 0x09: {
			int i1 = constantValue.getInt("index1");
			int i2 = constantValue.getInt("index2");
			return getConstantValue(constantPoolList, i1) + " " + getConstantValue(constantPoolList, i2);
		}
		case 0x0a: {
			int i1 = constantValue.getInt("index1");
			int i2 = constantValue.getInt("index2");
			return getConstantValue(constantPoolList, i1) + " " + getConstantValue(constantPoolList, i2);
		}
		case 0x0b: {
			int i1 = constantValue.getInt("index1");
			int i2 = constantValue.getInt("index2");
			return getConstantValue(constantPoolList, i1) + " " + getConstantValue(constantPoolList, i2);
		}
		case 0x0c: {
			int i1 = constantValue.getInt("index1");
			int i2 = constantValue.getInt("index2");
			return getConstantValue(constantPoolList, i1) + " " + getConstantValue(constantPoolList, i2);
		}
		case 0x0d: {
			int i2 = constantValue.getInt("index2");
			return getConstantValue(constantPoolList, i2);
		}
		case 0x10: {
			int i = constantValue.getInt("index");
			return getConstantValue(constantPoolList, i);
		}
		case 0x12: {
			int i2 = constantValue.getInt("index2");
			return getConstantValue(constantPoolList, i2);
		}
		}
		return "tag = " + tag;
	}

	/**
	 * 类的访问属性
	 * @param byteArray
	 * @return
	 */
	public static String getClassAccessFlags(byte[] byteArray) {
		int byteValue = u2ByteToInt(byteArray);
		StringBuilder flags = new StringBuilder();
		if ((byteValue & 0x0001) != 0) {
			flags.append("public ");
		}
		if ((byteValue & 0x0010) != 0) {
			flags.append("final ");
		}
		if ((byteValue & 0x0020) != 0) {
		}
		if ((byteValue & 0x0200) != 0) {
			flags.append("interface ");
		}
		if ((byteValue & 0x0400) != 0) {
			flags.append("abstract ");
		}
		if ((byteValue & 0x1000) != 0) {
		}
		if ((byteValue & 0x2000) != 0) {
			flags.append("Annotation ");
		} else if ((byteValue & 0x4000) != 0) {
			flags.append("enum ");
		} else {
			flags.append("class ");
		}
		return flags.toString();
	}

	/**
	 * 方法的访问属性
	 * @param byteArray
	 * @return
	 */
	public static String getMethodAccessFlags(byte[] byteArray) {
		int byteValue = u2ByteToInt(byteArray);
		StringBuilder flags = new StringBuilder();
		if ((byteValue & 0x0001) != 0) {
			flags.append("public ");
		}
		if ((byteValue & 0x0002) != 0) {
			flags.append("private ");
		}
		if ((byteValue & 0x0004) != 0) {
			flags.append("protected ");
		}
		if ((byteValue & 0x0008) != 0) {
			flags.append("static ");
		}
		if ((byteValue & 0x0010) != 0) {
			flags.append("final ");
		}
		if ((byteValue & 0x0020) != 0) {
			flags.append("synchronized ");
		}
		if ((byteValue & 0x0040) != 0) {
		}
		if ((byteValue & 0x0080) != 0) {
		}
		if ((byteValue & 0x0100) != 0) {
			flags.append("native ");
		}
		if ((byteValue & 0x0400) != 0) {
			flags.append("abstract ");
		}
		if ((byteValue & 0x0800) != 0) {
			flags.append("strictfp ");
		}

		return flags.toString();
	}

	/**
	 * 字段的访问属性
	 * @param byteArray
	 * @return
	 */
	public static String getAttributesAccessFlags(byte[] byteArray) {
		int byteValue = u2ByteToInt(byteArray);
		StringBuilder flags = new StringBuilder();
		if ((byteValue & 0x0001) != 0) {
			flags.append("public ");
		}
		if ((byteValue & 0x0002) != 0) {
			flags.append("private ");
		}
		if ((byteValue & 0x0004) != 0) {
			flags.append("protected ");
		}
		if ((byteValue & 0x0008) != 0) {
			flags.append("static ");
		}
		if ((byteValue & 0x0010) != 0) {
			flags.append("final ");
		}
		if ((byteValue & 0x0040) != 0) {
			flags.append("volatile ");
		}
		if ((byteValue & 0x0080) != 0) {
			flags.append("transient ");
		}
		if ((byteValue & 0x1000) != 0) {
		}
		if ((byteValue & 0x4000) != 0) {
			flags.append("enum ");
		}
		return flags.toString();
	}

	public static byte[] subByteArray(byte[] byteArray, int off, int length) {
		byte[] result = new byte[length];
		for (int i = 0; i < length; i++) {
			result[i] = byteArray[off + i];
		}
		return result;
	}
}
