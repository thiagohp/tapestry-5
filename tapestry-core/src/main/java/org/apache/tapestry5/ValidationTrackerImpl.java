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

package org.apache.tapestry5;

import org.apache.tapestry5.commons.util.CollectionFactory;
import org.apache.tapestry5.ioc.internal.util.InternalUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Standard implementation of {@link ValidationTracker}. Works pretty hard to ensure a minimum
 * amount of data is stored
 * in the HttpSession.
 */
public final class ValidationTrackerImpl extends BaseOptimizedSessionPersistedObject implements ValidationTracker, Serializable
{
    private static final long serialVersionUID = -8029192726659275677L;

    private static class FieldTracker implements Serializable
    {
        private static final long serialVersionUID = -3653306147088451811L;

        private final String validationId;

        private String input;

        private String errorMessage;

        FieldTracker(String validationId)
        {
            this.validationId = validationId;
        }
    }

    private List<String> extraErrors;

    private List<FieldTracker> fieldTrackers;

    // Rebuilt on-demand
    // Keyed on validationId

    private transient Map<String, FieldTracker> fieldToTracker;

    private void refreshFieldToTracker()
    {
        if (fieldToTracker != null)
            return;

        if (fieldTrackers == null)
            return;

        fieldToTracker = CollectionFactory.newMap();

        for (FieldTracker ft : fieldTrackers)
            fieldToTracker.put(ft.validationId, ft);
    }

    private String getKey(Field field)
    {
        return field.getControlName();
    }

    private FieldTracker get(Field field)
    {
        String key = getKey(field);

        refreshFieldToTracker();

        FieldTracker result = InternalUtils.get(fieldToTracker, key);

        if (result == null)
            result = new FieldTracker(key);

        return result;
    }

    private void store(FieldTracker fieldTracker)
    {
        if (fieldTrackers == null)
            fieldTrackers = CollectionFactory.newList();

        refreshFieldToTracker();

        String key = fieldTracker.validationId;

        if (!fieldToTracker.containsKey(key))
        {
            fieldTrackers.add(fieldTracker);
            fieldToTracker.put(key, fieldTracker);
        }

        markDirty();
    }

    public void clear()
    {
        extraErrors = null;
        fieldTrackers = null;
        fieldToTracker = null;

        markDirty();
    }

    public String getError(Field field)
    {
        return get(field).errorMessage;
    }

    public List<String> getErrors()
    {
        List<String> result = CollectionFactory.newList();

        if (extraErrors != null)
            result.addAll(extraErrors);

        if (fieldTrackers != null)
        {
            for (FieldTracker ft : fieldTrackers)
            {
                String errorMessage = ft.errorMessage;

                if (errorMessage != null)
                    result.add(errorMessage);
            }
        }

        return result;
    }

    public List<String> getUnassociatedErrors()
    {
        if (extraErrors == null)
        {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(extraErrors);
    }

    public boolean getHasErrors()
    {
        return !getErrors().isEmpty();
    }

    public String getInput(Field field)
    {
        return get(field).input;
    }

    public boolean inError(Field field)
    {
        return InternalUtils.isNonBlank(get(field).errorMessage);
    }

    public void recordError(Field field, String errorMessage)
    {
        FieldTracker ft = get(field);

        ft.errorMessage = errorMessage;

        store(ft);
    }

    public void recordError(String errorMessage)
    {
        if (extraErrors == null)
            extraErrors = CollectionFactory.newList();

        extraErrors.add(errorMessage);

        markDirty();
    }

    public void recordInput(Field field, String input)
    {
        FieldTracker ft = get(field);

        ft.input = input;

        store(ft);
    }
}
