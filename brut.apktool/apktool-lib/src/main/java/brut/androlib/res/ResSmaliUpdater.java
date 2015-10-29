/**
 * Copyright 2014 Ryszard Wiśniewski <brut.alll@gmail.com>
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package brut.androlib.res;

import brut.androlib.AndrolibException;
import brut.androlib.err.UndefinedResObject;
import brut.androlib.res.data.ResResSpec;
import brut.androlib.res.data.ResTable;
import brut.directory.Directory;
import brut.directory.DirectoryException;
import brut.directory.FileDirectory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

/**
 * @author Ryszard Wiśniewski <brut.alll@gmail.com>
 */
public class ResSmaliUpdater {

    public void tagResIDs(ResTable resTable, File smaliDir)
            throws AndrolibException {

        /**定义smali路径*/
        Directory dir = null;
        try {
            dir = new FileDirectory(smaliDir);
        } catch (DirectoryException ex) {
            throw new AndrolibException("Could not tag res IDs", ex);
        }
        /**遍历smali目录*/
        for (String fileName : dir.getFiles(true)) {
            try {
                tagResIdsForFile(resTable, dir, fileName);
            } catch (IOException ex) {
                throw new AndrolibException("Could not tag resIDs for file: "
                        + fileName, ex);
            } catch (DirectoryException ex) {
                throw new AndrolibException("Could not tag resIDs for file: "
                        + fileName, ex);
            } catch (AndrolibException ex) {
                throw new AndrolibException("Could not tag resIDs for file: "
                        + fileName, ex);
            }
        }
    }


    /**修改资源id*/
    public void updateResIDs(ResTable resTable, File smaliDir)
            throws AndrolibException {
        try {
            Directory dir = new FileDirectory(smaliDir);
            //遍历所有smali文件
            for (String fileName : dir.getFiles(true)) {
                Iterator<String> it = IOUtils.readLines(
                        dir.getFileInput(fileName)).iterator();
                PrintWriter out = new PrintWriter(dir.getFileOutput(fileName));
                //迭代文件每一行
                while (it.hasNext()) {
                    String line = it.next();
                    out.println(line);
                    //如果不符合名称正则，下一行
                    Matcher m1 = RES_NAME_PATTERN.matcher(line);
                    if (!m1.matches()) {
                        continue;
                    }
                    //如果id不符合 抛异常
                    Matcher m2 = RES_ID_PATTERN.matcher(it.next());
                    if (!m2.matches()) {
                        throw new AndrolibException();
                    }
                    //从资源表中根据找到该包（如com.kuaifa.xx), 再在其下找到该分类（如layout，string）, 再根据维度找到该res的id
                    int resID = resTable.getPackage(m1.group(1))
                            .getType(m1.group(2)).getResSpec(m1.group(3))
                            .getId().id;
                    //将id写入smali
                    if (m2.group(1) != null) {
                        out.println(String.format(RES_ID_FORMAT_FIELD,
                                m2.group(1), resID));
                    } else {
                        out.println(String.format(RES_ID_FORMAT_CONST,
                                m2.group(2), resID));
                    }
                }
                out.close();
            }
        } catch (IOException ex) {
            throw new AndrolibException("Could not tag res IDs for: "
                    + smaliDir.getAbsolutePath(), ex);
        } catch (DirectoryException ex) {
            throw new AndrolibException("Could not tag res IDs for: "
                    + smaliDir.getAbsolutePath(), ex);
        }
    }

    /**
     * 将资源表中的id写到smali文件
     */
    private void tagResIdsForFile(ResTable resTable, Directory dir,
                                  String fileName) throws IOException, DirectoryException,
            AndrolibException {
        //读入smali文件对文件每一行进行迭代
        Iterator<String> it = IOUtils.readLines(dir.getFileInput(fileName))
                .iterator();
        //同时打开对smali文件的写接口
        PrintWriter out = new PrintWriter(dir.getFileOutput(fileName));
        //对每一行操作
        while (it.hasNext()) {
            String line = it.next();
            //如果该行编码符合资源名称的正则表达式，直接写入smali
            if (RES_NAME_PATTERN.matcher(line).matches()) {
                out.println(line);
                out.println(it.next());
                continue;
            }
            //如果该行编码符合资源id的正则表达式
            Matcher m = RES_ID_PATTERN.matcher(line);
            if (m.matches()) {
                int resID = parseResID(m.group(3));
                if (resID != -1) {
                    try {
                        //如果资源id不等于-1，到资源表中找到该资源更多的描述，
                        ResResSpec spec = resTable.getResSpec(resID);
                        out.println(String.format(RES_NAME_FORMAT,
                                spec.getFullName()));
                    } catch (UndefinedResObject ex) {
                        if (!R_FILE_PATTERN.matcher(fileName).matches()) {
                            LOGGER.warning(String.format(
                                    "Undefined resource spec in %s: 0x%08x",
                                    fileName, resID));
                        }
                    }
                }
            }
            //写入该行
            out.println(line);
        }
        out.close();
    }

    private int parseResID(String resIDHex) {
        if (resIDHex.endsWith("ff")) {
            return -1;
        }
        int resID = Integer.valueOf(resIDHex, 16);
        if (resIDHex.length() == 4) {
            resID = resID << 16;
        }
        return resID;
    }

    private final static String RES_ID_FORMAT_FIELD = ".field %s:I = 0x%08x";
    private final static String RES_ID_FORMAT_CONST = "    const %s, 0x%08x";
    private final static Pattern RES_ID_PATTERN = Pattern
            .compile("^(?:\\.field (.+?):I =|    const(?:|/(?:|high)16) ([pv]\\d+?),) 0x(7[a-f]0[1-9a-f](?:|[0-9a-f]{4}))$");
    private final static String RES_NAME_FORMAT = "# APKTOOL/RES_NAME: %s";
    private final static Pattern RES_NAME_PATTERN = Pattern
            .compile("^# APKTOOL/RES_NAME: ([a-zA-Z0-9.]+):([a-z]+)/([a-zA-Z0-9._]+)$");

    private final static Pattern R_FILE_PATTERN = Pattern
            .compile(".*R\\$[a-z]+\\.smali$");

    private final static Logger LOGGER = Logger.getLogger(ResSmaliUpdater.class
            .getName());
}
