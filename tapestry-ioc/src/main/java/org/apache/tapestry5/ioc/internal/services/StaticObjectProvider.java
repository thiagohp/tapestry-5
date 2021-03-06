// Copyright 2011 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry5.ioc.internal.services;

import org.apache.tapestry5.commons.AnnotationProvider;
import org.apache.tapestry5.commons.ObjectLocator;
import org.apache.tapestry5.commons.ObjectProvider;

/**
 * Provides a single object of a given type.
 *
 * @since 5.3
 */
public class StaticObjectProvider<S> implements ObjectProvider
{
    private final Class<S> valueType;

    private final S value;

    public StaticObjectProvider(Class<S> valueType, S value)
    {
        this.valueType = valueType;
        this.value = value;

        assert valueType != null;
        assert value != null;
    }


    @Override
    public <T> T provide(Class<T> objectType, AnnotationProvider annotationProvider, ObjectLocator locator)
    {
        if (objectType == valueType)
        {
            return objectType.cast(value);
        }

        return null;
    }
}
