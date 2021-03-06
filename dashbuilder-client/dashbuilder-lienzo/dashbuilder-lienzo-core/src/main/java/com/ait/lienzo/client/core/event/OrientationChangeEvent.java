/*
   Copyright (c) 2014,2015 Ahome' Innovation Technologies. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.ait.lienzo.client.core.event;

import com.ait.lienzo.shared.core.types.ScreenOrientation;
import com.google.gwt.event.shared.GwtEvent;

public class OrientationChangeEvent extends GwtEvent<OrientationChangeHandler>
{
    private final int                                  m_width;

    private final int                                  m_height;

    private final ScreenOrientation                    m_orientation;

    public static final Type<OrientationChangeHandler> TYPE = new Type<OrientationChangeHandler>();

    public OrientationChangeEvent(ScreenOrientation orientation, int width, int height)
    {
        m_width = width;

        m_height = height;

        m_orientation = orientation;
    }

    @Override
    public Type<OrientationChangeHandler> getAssociatedType()
    {
        return TYPE;
    }

    public int getWidth()
    {
        return m_width;
    }

    public int getHeight()
    {
        return m_height;
    }

    public ScreenOrientation getOrientation()
    {
        return m_orientation;
    }

    @Override
    protected void dispatch(OrientationChangeHandler handler)
    {
        handler.onOrientationChange(this);
    }
}