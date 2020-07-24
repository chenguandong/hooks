package com.tl.inspectionretrieval.util;

import android.content.Context;

import com.comtop.EimCloud;
import com.comtop.util.ThreadUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Autour: 1211john
 * Date: 2020/6/29 14:14
 * FileName: FileSaveManager
 * Desciption:
 * @author 1211john
 */
public class FileSaveManager {
    private static volatile FileSaveManager instance;

    public static FileSaveManager getInstance() {
        if (instance == null) {
            synchronized (FileSaveManager.class) {
                if (instance == null) {
                    instance = new FileSaveManager();
                }
            }
        }
        return instance;
    }


    /**
     * 将assets文件夹下的文件拷贝到/data/data/下
     *
     * @param context
     * @param fileName
     */
    private void copyDbFile(final Context context, final String fileName, final String openPath, final String title, final boolean islast) {
        ThreadUtil.runOnThread("saveFiles", new Runnable() {
            @Override
            public void run() {
                InputStream in = null;
                FileOutputStream out = null;
                String path = context.getFilesDir().getAbsolutePath() + "/audiofiles/";
                File file = new File(path + fileName);

                try {
                    //创建文件夹
                    File filePath = new File(path);
                    if (!filePath.exists()) {
                        filePath.mkdirs();
                    }

                    if (file.exists()) {
                        return;
                    }
                    in = context.getAssets().open(fileName); // 从assets目录下复制
                    out = new FileOutputStream(file);
                    int length = -1;
                    byte[] buf = new byte[1024];
                    while ((length = in.read(buf)) != -1) {
                        out.write(buf, 0, length);
                    }
                    out.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                        if (out != null) {
                            out.close();
                        }
                        SPUtils.setHtmlPath(EimCloud.getContext(), "file:///" + path + openPath);

                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

    }


    /**
     * 存资源在data中
     *
     * @param ctxDealFile applicationContext
     * @param title       网页标题title
     * @param path        assets目录下文件(alert.html)或文件夹路径(text)
     * @param openPath    入口html路径(alert.html)
     */
    public void deepFile(Context ctxDealFile, String title, String path, String openPath) {
        boolean isLast = false;
        try {
            String[] str = ctxDealFile.getAssets().list(path);
            //如果是目录
            if (str.length > 0) {
                File file = new File(ctxDealFile.getFilesDir().getAbsolutePath() + "/audiofiles/" + path);
                if (file.exists()) {
                    // TODO: 2020/6/29  目录已存在防止点击没反应 处理逻辑待定
                    path = path + "/" + str[0];
                    copyDbFile(ctxDealFile, path, openPath, title, true);
                } else {
                    file.mkdirs();
                    StringBuilder stringBuilder = new StringBuilder(path);
                    for (int i = 0; i < str.length; i++) {
                        isLast = i == str.length - 1;
                        stringBuilder.append("/").append(str[i]);
                        copyDbFile(ctxDealFile, stringBuilder.toString(), openPath, title, isLast);
                        stringBuilder = new StringBuilder(stringBuilder.substring(0, path.lastIndexOf('/')));
                    }
                }
            } else {//如果是文件
                copyDbFile(ctxDealFile, path, openPath, title, true);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
