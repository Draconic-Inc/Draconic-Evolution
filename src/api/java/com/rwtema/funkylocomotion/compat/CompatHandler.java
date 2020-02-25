package com.rwtema.funkylocomotion.compat;

import net.minecraft.util.StringUtils;

import java.util.Map;

public abstract class CompatHandler {
//	public static void initCompat(ASMDataTable asmData) {
//		for (ASMDataTable.ASMData data : asmData.getAll(ModCompat.class.getName())) {
//			Map<String, Object> annotationInfo = data.getAnnotationInfo();
//			String modid = annotationInfo.getOrDefault("modid", "").toString();
//			String classname = annotationInfo.getOrDefault("classname", "").toString();
//
//			boolean hasModID = !StringUtils.isNullOrEmpty(modid);
//			boolean hasClassname = !StringUtils.isNullOrEmpty(classname);
//			if (!hasModID && !hasClassname) {
//				throw new IllegalStateException("Both Mod ID and classname are blank");
//			}
//
//			boolean flag = hasModID && (Loader.isModLoaded(modid) || ModAPIManager.INSTANCE.hasAPI(modid));
//			if (!flag && hasClassname) {
//				try {
//					Class.forName(classname);
//					flag = true;
//				} catch (ClassNotFoundException ignore) {
//					flag = false;
//				}
//			}
//
//			if (flag) {
//				try {
//					@SuppressWarnings("unchecked")
//					Class<? extends CompatHandler> name = (Class<? extends CompatHandler>) Class.forName(data.getClassName());
//					CompatHandler compatHandler = name.newInstance();
//					compatHandler.init();
//				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
//				    throw new RuntimeException(e);
//				}
//			}
//		}
//
//	}

	public abstract void init();
}