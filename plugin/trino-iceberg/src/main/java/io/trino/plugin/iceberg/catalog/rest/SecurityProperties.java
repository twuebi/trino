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

import java.util.Map;

public interface SecurityProperties
{
    Map<String, String> get();

    /**
     * Per-user property overrides merged into {@link org.apache.iceberg.catalog.SessionCatalog.SessionContext}
     * properties so Iceberg's contextual AuthSession can authenticate as the Trino session user.
     * The default implementation returns an empty map; implementations that support per-user
     * authentication override this method.
     */
    default Map<String, String> sessionOverrides(String user)
    {
        return ImmutableMap.of();
    }
}
