/**
 *  Copyright 2014 Ryszard Wi艣niewski <brut.alll@gmail.com>
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

/**
 * @author Ryszard Wi艣niewski <brut.alll@gmail.com>
 */
 // 暂存数据的实体类
public class ResBoolValue extends ResScalarValue {
    private final boolean mValue;

    public ResBoolValue(boolean value, int rawIntValue, String rawValue) {
        super("bool", rawIntValue, rawValue);
        this.mValue = value;
    }

    public boolean getValue() {
        return mValue;
    }

    @Override
    protected String encodeAsResXml() {
        return mValue ? "true" : "false";
    }
}
