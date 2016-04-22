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

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.AttributeSource;

import java.io.IOException;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UkPostcodeTokenFilter extends TokenFilter {

    private final Stack<String> synonymStack;
    private AttributeSource.State current;

    private final CharTermAttribute termAttribute;
    private final PositionIncrementAttribute positionIncrementAttribute;
    private final boolean includeOutcode;

    /**
     * Construct a token stream filtering the given input.
     *
     * @param input source TokenStream
     */
    protected UkPostcodeTokenFilter(TokenStream input, boolean includeOutcode) {
        super(input);
        this.synonymStack = new Stack<>();
        this.termAttribute = addAttribute(CharTermAttribute.class);
        this.positionIncrementAttribute = addAttribute(PositionIncrementAttribute.class);
        this.includeOutcode = includeOutcode;
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (!synonymStack.isEmpty()) {
            String syn = synonymStack.pop();
            restoreState(current);
            termAttribute.setEmpty();
            termAttribute.append(syn);
            positionIncrementAttribute.setPositionIncrement(0);
            return true;
        }

        if (!input.incrementToken()) {
            return false;
        }
        if (addAliasesToStack()) {
            current = captureState();
        }
        return true;
    }

    private boolean addAliasesToStack() throws IOException {
        String value = String.valueOf(termAttribute.buffer(), 0, termAttribute.length());
        UkPostcodeMatcher matcher = new UkPostcodeMatcher(value);
        if (matcher.isPostcode()) {
            termAttribute.setEmpty();
            synonymStack.push(matcher.getPostcode());
            if (includeOutcode) {
                synonymStack.push(matcher.getOutcode());
            }
            return true;
        }
        return false;
    }
}
