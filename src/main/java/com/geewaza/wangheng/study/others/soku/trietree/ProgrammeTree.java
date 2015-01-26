package com.geewaza.wangheng.study.others.soku.trietree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class ProgrammeTree<T extends Comparable<T>> {
	
	private TreeNode<T> root;
	
	public ProgrammeTree() {
		root = new TreeNode<T>(null);
	}
	
	public TreeNode<T> getRoot() {
		return root;
	}
	
	public void insert(String keys, T data, int nodeDataMaxSize, boolean keepSpace, boolean fuzzy) {
		if (StringUtils.isBlank(keys)) {
			throw new IllegalArgumentException("Key = " + keys);
		}
		if (data == null) {
			throw new NullPointerException("Data is null");
		}
		char[] keychars = keys.toCharArray();
		
		TreeNode<T> currentNode = root;
		for (char keychar : keychars) {
			String key = String.valueOf(keychar);
			if (StringUtils.isBlank(key) && !keepSpace) {
				continue;
			}
			TreeNode<T> childNode = currentNode.getChild(key);
			if (fuzzy) {
				currentNode.addData(data, nodeDataMaxSize);
			}
			if (childNode == null) {
				//加入新的节点
				childNode = new TreeNode<T>(key);
				currentNode.insertChild(key, childNode);
			}
			currentNode = childNode;
		}
		currentNode.addData(data, nodeDataMaxSize);
	}
	
	public T searchFastDeepFirst(String keys, boolean keepspace) {
		if (StringUtils.isBlank(keys)) {
			throw new IllegalArgumentException("Key = " + keys);
		}
		T yeild = null;
		char[] keychars = keys.toCharArray();
		for (int i = 0; i < keychars.length; i++) {
			TreeNode<T> currentNode = root;
			for (int j = i; j < keychars.length; j++) {
				String key = String.valueOf(keychars[j]);
				if (StringUtils.isBlank(key) && !keepspace) {
					continue;
				}
				TreeNode<T> childNode = currentNode.getChild(key);
				if (childNode == null) {
					break;
				} else {
					List<T> datas = childNode.getDatas();
					if (datas.size() > 0) {
						yeild = datas.get(0);//取第一个数据
					}
					currentNode = childNode;
				}
			}
			if (yeild != null) {
				return yeild;
			}
		}
		return yeild;
	}
	
	static class TreeNode<T extends Comparable<T>> {
		private String key;
		private List<T> datas;
		private Map<String, TreeNode<T>> children;
		
		public TreeNode(String key) {
			this.key = key;
			this.children = new HashMap<String, ProgrammeTree.TreeNode<T>>();
			this.datas = new ArrayList<T>();
		}
		
		public String getKey() {
			return key;
		}
		
		public List<T> getDatas() {
			return datas;
		}
		
		public TreeNode<T> getChild(String key) {
			return children.get(key);
		}
		
		public void insertChild(String key, TreeNode<T> node) {
			children.put(key, node);
		}
		
		public void addData(T data, int maxSize) {
			if (!datas.contains(data)) {
				datas.add(data);
			}
			Collections.sort(datas, new Comparator<T>() {
				@Override
				public int compare(T o1, T o2) {
					return o1.compareTo(o2);
				}
			});
			if (maxSize != 0 && datas.size() > maxSize) {
				datas = datas.subList(0, maxSize);
			}
		}
	}
}
