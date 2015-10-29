/**
 *  Copyright 2014 Ryszard Wiśniewski <brut.alll@gmail.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package brut.androlib.res.data.value;

import brut.androlib.AndrolibException;
import brut.androlib.res.data.ResResource;
import brut.androlib.res.xml.ResValuesXmlSerializable;
import brut.androlib.res.xml.ResXmlEncodable;
import brut.androlib.res.xml.ResXmlEncoders;
import java.io.IOException;
import org.xmlpull.v1.XmlSerializer;

/**
 * 将res中用到的基本类型抽象出来一个scalar标量父类
 * @author Ryszard Wiśniewski <brut.alll@gmail.com>
 */
public abstract class ResScalarValue extends ResIntBasedValue implements
        ResXmlEncodable, ResValuesXmlSerializable {
    /**标量的类型 如float，string, int*/
    protected final String mType;
    /**原生值*/
    protected final String mRawValue;

    protected ResScalarValue(String type, int rawIntValue, String rawValue) {
        super(rawIntValue);
        mType = type;
        mRawValue = rawValue;
    }

    @Override
    public String encodeAsResXmlAttr() throws AndrolibException {
        if (mRawValue != null) {
            return mRawValue;
        }
        return encodeAsResXml();
    }

    public String encodeAsResXmlItemValue() throws AndrolibException {
        return encodeAsResXmlValue();
    }

    /**封装resxml资源值*/
    @Override
    public String encodeAsResXmlValue() throws AndrolibException {
        if (mRawValue != null) {
            return mRawValue;
        }
        return encodeAsResXml();
    }

    /**替换转义*/
    public String encodeAsResXmlNonEscapedItemValue() throws AndrolibException {
        return encodeAsResXmlValue().replace("&amp;", "&").replace("&lt;","<");
    }

    public boolean hasMultipleNonPositionalSubstitutions() throws AndrolibException {
        return ResXmlEncoders.hasMultipleNonPositionalSubstitutions(mRawValue);
    }

    /**序列化成资源xml*/
    @Override
    public void serializeToResValuesXml(XmlSerializer serializer,
                                        ResResource res) throws IOException, AndrolibException {
        String type = res.getResSpec().getType().getName();
        /**验证是否该属性的类型  比如true， 34dp */
        boolean item = !"reference".equals(mType) && !type.equals(mType);

        String body = encodeAsResXmlValue();

        // check for resource reference
        if (!type.equalsIgnoreCase("color")) {
            if (body.contains("@")) {
                if (!res.getFilePath().contains("string")) {
                    item = true;
                }
            }
        }

        /**构造资源值的xml节点*/
        String tagName = item ? "item" : type;

        serializer.startTag(null, tagName);
        if (item) {
            serializer.attribute(null, "type", type);
        }
        serializer.attribute(null, "name", res.getResSpec().getName());

        serializeExtraXmlAttrs(serializer, res);

        if (!body.isEmpty()) {
            serializer.ignorableWhitespace(body);
        }

        serializer.endTag(null, tagName);
    }

    public String getType() {
        return mType;
    }

    protected void serializeExtraXmlAttrs(XmlSerializer serializer,
                                          ResResource res) throws IOException {
    }

    protected abstract String encodeAsResXml() throws AndrolibException;
}
