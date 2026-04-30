/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.trino.plugin.iceberg.catalog.rest;

import com.google.common.collect.ImmutableMap;
import io.trino.filesystem.s3.S3FileSystemConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TestSigV4AwsProperties
{
    @Test
    void testSessionOverridesEmptyWhenNoTemplate()
    {
        SigV4AwsProperties properties = new SigV4AwsProperties(
                new IcebergRestCatalogSigV4Config(),
                new S3FileSystemConfig()
                        .setRegion("us-east-1")
                        .setIamRole("arn:aws:iam::123456789:role/service"));

        assertThat(properties.sessionOverrides("alice")).isEmpty();
    }

    @Test
    void testSessionOverridesSubstitutesUser()
    {
        SigV4AwsProperties properties = new SigV4AwsProperties(
                new IcebergRestCatalogSigV4Config()
                        .setUserIamRoleTemplate("arn:aws:iam::123456789:role/trino/${USER}"),
                new S3FileSystemConfig()
                        .setRegion("us-east-1")
                        .setIamRole("arn:aws:iam::123456789:role/service"));

        assertThat(properties.sessionOverrides("alice"))
                .isEqualTo(ImmutableMap.of("client.credentials-provider.aws_iam_role", "arn:aws:iam::123456789:role/trino/alice"));

        assertThat(properties.sessionOverrides("bob"))
                .isEqualTo(ImmutableMap.of("client.credentials-provider.aws_iam_role", "arn:aws:iam::123456789:role/trino/bob"));
    }

    @Test
    void testGetKeepsServiceRoleStatic()
    {
        SigV4AwsProperties properties = new SigV4AwsProperties(
                new IcebergRestCatalogSigV4Config()
                        .setUserIamRoleTemplate("arn:aws:iam::123456789:role/trino/${USER}"),
                new S3FileSystemConfig()
                        .setRegion("us-east-1")
                        .setIamRole("arn:aws:iam::123456789:role/service"));

        assertThat(properties.get())
                .containsEntry("client.credentials-provider.aws_iam_role", "arn:aws:iam::123456789:role/service");
    }
}
