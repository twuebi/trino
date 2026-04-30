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
import jakarta.validation.constraints.AssertTrue;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.airlift.configuration.testing.ConfigAssertions.assertFullMapping;
import static io.airlift.configuration.testing.ConfigAssertions.assertRecordedDefaults;
import static io.airlift.configuration.testing.ConfigAssertions.recordDefaults;
import static io.airlift.testing.ValidationAssertions.assertFailsValidation;
import static io.airlift.testing.ValidationAssertions.assertValidates;

final class TestIcebergRestCatalogSigV4Config
{
    @Test
    void testDefaults()
    {
        assertRecordedDefaults(recordDefaults(IcebergRestCatalogSigV4Config.class)
                .setSigningName("execute-api")
                .setUserIamRoleTemplate(null));
    }

    @Test
    void testExplicitPropertyMappings()
    {
        Map<String, String> properties = ImmutableMap.<String, String>builder()
                .put("iceberg.rest-catalog.signing-name", "glue")
                .put("iceberg.rest-catalog.user-iam-role-template", "arn:aws:iam::123456789:role/trino/${USER}")
                .buildOrThrow();

        IcebergRestCatalogSigV4Config expected = new IcebergRestCatalogSigV4Config()
                .setSigningName("glue")
                .setUserIamRoleTemplate("arn:aws:iam::123456789:role/trino/${USER}");

        assertFullMapping(properties, expected);
    }

    @Test
    void testUserIamRoleTemplateRequiresUserPlaceholder()
    {
        assertValidates(new IcebergRestCatalogSigV4Config());
        assertValidates(new IcebergRestCatalogSigV4Config()
                .setUserIamRoleTemplate("arn:aws:iam::123456789:role/trino/${USER}"));
        assertFailsValidation(
                new IcebergRestCatalogSigV4Config().setUserIamRoleTemplate("arn:aws:iam::123456789:role/trino/static"),
                "userIamRoleTemplateValid",
                "iceberg.rest-catalog.user-iam-role-template must contain ${USER}",
                AssertTrue.class);
    }
}
