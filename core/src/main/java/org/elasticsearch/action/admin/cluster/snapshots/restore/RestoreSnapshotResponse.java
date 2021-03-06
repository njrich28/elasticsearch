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

package org.elasticsearch.action.admin.cluster.snapshots.restore;

import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.snapshots.RestoreInfo;

import java.io.IOException;

/**
 * Contains information about restores snapshot
 */
public class RestoreSnapshotResponse extends ActionResponse implements ToXContent {

    @Nullable
    private RestoreInfo restoreInfo;

    RestoreSnapshotResponse(@Nullable RestoreInfo restoreInfo) {
        this.restoreInfo = restoreInfo;
    }

    RestoreSnapshotResponse() {
    }

    /**
     * Returns restore information if snapshot was completed before this method returned, null otherwise
     *
     * @return restore information or null
     */
    public RestoreInfo getRestoreInfo() {
        return restoreInfo;
    }

    @Override
    public void readFrom(StreamInput in) throws IOException {
        super.readFrom(in);
        restoreInfo = RestoreInfo.readOptionalRestoreInfo(in);
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        super.writeTo(out);
        out.writeOptionalStreamable(restoreInfo);
    }

    public RestStatus status() {
        if (restoreInfo == null) {
            return RestStatus.ACCEPTED;
        }
        return restoreInfo.status();
    }

    static final class Fields {
        static final String SNAPSHOT = "snapshot";
        static final String ACCEPTED = "accepted";
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, ToXContent.Params params) throws IOException {
        if (restoreInfo != null) {
            builder.field(Fields.SNAPSHOT);
            restoreInfo.toXContent(builder, params);
        } else {
            builder.field(Fields.ACCEPTED, true);
        }
        return builder;
    }
}
