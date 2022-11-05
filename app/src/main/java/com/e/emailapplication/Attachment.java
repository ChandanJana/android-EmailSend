package com.e.emailapplication;

import javax.activation.DataSource;

/**
 * Created by Chandan on 05/07/21
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
public class Attachment
{
    protected final DataSource dataSource;

    public Attachment( final DataSource dataSource )
    {
        this.dataSource = dataSource;
    }

    /**
     * BUG: InputStream has to be new instance every call.
     * Stream is read to retrieve Content-Type and by SMTP write to socket,
     * but stream is read once, reading twice will result in empty result.
     *
     * To retrive Content-Type, library has to copy the stream (be a middleman) or
     * extend itself with a peak command.
     *
     * public InputStream getInputStream()
     */
    DataSource getDataSource()
    {
        return dataSource;
    }
}