/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.index.analysis;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UkPostcodeMatcher {
    public static final Pattern POSTCODE_REGEX =
            Pattern.compile("(GIR 0AA)|((([A-Z-[QVX]][0-9][0-9]?)|(([A-Z-[QVX]][A-Z-[IJZ]][0-9][0-9]?)|(([A-Z-[QVX]][0-9][A-HJKPSTUW])" +
                    "|([A-Z-[QVX]][A-Z-[IJZ]][0-9][ABEHMNPRVWXY])))) *([0-9][A-Z-[CIKMOV]]{2}))", Pattern.CASE_INSENSITIVE);
    public static final int OUTCODE_GROUP = 3;
    public static final int INCODE_GROUP = 10;

    private final Matcher matcher;

    public UkPostcodeMatcher(CharSequence input) {
        matcher = POSTCODE_REGEX.matcher(input);
    }

    public boolean isPostcode() {
        return matcher.matches();
    }

    public String getOutcode() {
        return matcher.group(OUTCODE_GROUP).toUpperCase(Locale.UK);
    }

    public String getIncode() {
        return matcher.group(INCODE_GROUP).toUpperCase(Locale.UK);
    }

    public String getPostcode() {
        return getOutcode() + " " + getIncode();
    }

}
