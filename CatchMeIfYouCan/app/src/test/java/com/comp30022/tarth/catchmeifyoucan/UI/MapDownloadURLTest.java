package com.comp30022.tarth.catchmeifyoucan.UI;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MapDownloadURL.class)
public class MapDownloadURLTest {

    MapDownloadURL mapDownloadURL = new MapDownloadURL();

    @Test
    public void testReadingUrl() throws Exception {
        String mUrlMock = PowerMockito.mock(String.class);
        URL urlMock = PowerMockito.mock(URL.class);
        HttpURLConnection urlConnectionMock = PowerMockito.mock(HttpURLConnection.class);
        InputStream inputStreamMock = PowerMockito.mock(InputStream.class);
        InputStreamReader inputStreamReaderMock = PowerMockito.mock(InputStreamReader.class);
        BufferedReader bufferedReaderMock = PowerMockito.mock(BufferedReader.class);
        StringBuffer stringBufferMock = PowerMockito.mock(StringBuffer.class);

        PowerMockito.whenNew(URL.class).withArguments(Mockito.anyString()).thenReturn(urlMock);
        PowerMockito.when(urlMock.openConnection()).thenReturn(urlConnectionMock);
        PowerMockito.doNothing().when(urlConnectionMock).connect();
        PowerMockito.when(urlConnectionMock.getInputStream()).thenReturn(inputStreamMock);
        PowerMockito.whenNew(InputStreamReader.class).withArguments(inputStreamMock).thenReturn(inputStreamReaderMock);
        PowerMockito.whenNew(BufferedReader.class).withArguments(inputStreamReaderMock).thenReturn(bufferedReaderMock);
        PowerMockito.whenNew(StringBuffer.class).withNoArguments().thenReturn(stringBufferMock);
        PowerMockito.when(bufferedReaderMock.readLine()).thenReturn(null);
        PowerMockito.when(stringBufferMock.toString()).thenReturn("data");
        PowerMockito.doNothing().when(bufferedReaderMock).close();

        String data = mapDownloadURL.readUrl(mUrlMock);
        assertEquals("data", data);
    }

}