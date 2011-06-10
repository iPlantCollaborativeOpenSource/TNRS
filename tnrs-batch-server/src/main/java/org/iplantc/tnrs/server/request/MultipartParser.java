package org.iplantc.tnrs.server.request;



import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public class MultipartParser
{
  /*private InputStream in;
  private String boundary;
  private FilePart lastFilePart;
  private byte[] buf = new byte[8192];

  private static String DEFAULT_ENCODING = "ISO-8859-1";

  private String encoding = DEFAULT_ENCODING;

  public MultipartParser(HttpExchange exchange, int paramInt)
    throws IOException
  {
    this(exchange, paramInt, true, true);
  }

  public MultipartParser(HttpExchange exchange, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException
  {
    String localObject1 = null;
    Headers headers = exchange.getRequestHeaders();
    String str1 = headers.get("Content-Type").get(0);
    String str2 = null;

    if ((str1 == null) && (str2 != null)) {
      localObject1 = str2;
    }
    else if ((str2 == null) && (str1 != null)) {
      localObject1 = str1;
    }
    else if ((str1 != null) && (str2 != null)) {
      localObject1 = (str1.length() > str2.length()) ? str1 : str2;
    }

    if ((localObject1 == null) || (!localObject1.toLowerCase().startsWith("multipart/form-data")))
    {
      throw new IOException("Posted content type isn't multipart/form-data");
    }

    int i = Integer.parseInt(headers.get("Content-Length").get(0));
    if (i > paramInt) {
      throw new IOException("Posted content length of " + i + " exceeds limit of " + paramInt);
    }

    String str3 = extractBoundary(localObject1);
    if (str3 == null) {
      throw new IOException("Separation boundary was not specified");
    }

    InputStream requestInputStream = exchange.getRequestBody();
    if (paramBoolean1) {
    	requestInputStream = new BufferedInputStream(requestInputStream);
    }
    if (paramBoolean2) {
      localObject2 = new LimitedServletInputStream((ServletInputStream)localObject2, i);
    }

    this.in = ((ServletInputStream)localObject2);
    this.boundary = str3;

    String str4 = readLine();
    if (str4 == null) {
      throw new IOException("Corrupt form data: premature ending");
    }

    if (!str4.startsWith(str3))
      throw new IOException("Corrupt form data: no leading boundary: " + str4 + " != " + str3);
  }

  public void setEncoding(String paramString)
  {
    this.encoding = paramString;
  }

  public Part readNextPart()
    throws IOException
  {
    if (this.lastFilePart != null) {
      this.lastFilePart.getInputStream().close();
      this.lastFilePart = null;
    }

    Vector localVector = new Vector();

    Object localObject1 = readLine();
    if (localObject1 == null)
    {
      return null;
    }
    if (((String)localObject1).length() == 0)
    {
      return null;
    }

    do
    {
      str1 = null;
      int i = 1;
      while (i != 0) {
        str1 = readLine();
        if ((str1 != null) && (((str1.startsWith(" ")) || (str1.startsWith("\t")))))
        {
          localObject1 = (String)localObject1 + str1;
        }
        else {
          i = 0;
        }
      }

      localVector.addElement(localObject1);
      localObject1 = str1;
    }
    while ((localObject1 != null) && (((String)localObject1).length() > 0));

    if (localObject1 == null) {
      return null;
    }

    String str1 = null;
    String str2 = null;
    String str3 = null;
    Object localObject2 = "text/plain";

    Enumeration localEnumeration = localVector.elements();
    while (localEnumeration.hasMoreElements()) {
      String str4 = (String)localEnumeration.nextElement();
      Object localObject3;
      if (str4.toLowerCase().startsWith("content-disposition:"))
      {
        localObject3 = extractDispositionInfo(str4);

        str1 = localObject3[1];
        str2 = localObject3[2];
        str3 = localObject3[3];
      } else {
        if (!str4.toLowerCase().startsWith("content-type:"))
          continue;
        localObject3 = extractContentType(str4);
        if (localObject3 != null) {
          localObject2 = localObject3;
        }
      }

    }

    if (str2 == null)
    {
      return new ParamPart(str1, this.in, this.boundary, this.encoding);
    }

    if (str2.equals("")) {
      str2 = null;
    }
    this.lastFilePart = new FilePart(str1, this.in, this.boundary, (String)localObject2, str2, str3);

    return (Part)(Part)(Part)this.lastFilePart;
  }

  private String extractBoundary(String paramString)
  {
    int i = paramString.lastIndexOf("boundary=");
    if (i == -1) {
      return null;
    }
    String str = paramString.substring(i + 9);
    if (str.charAt(0) == '"')
    {
      i = str.lastIndexOf('"');
      str = str.substring(1, i);
    }

    str = "--" + str;

    return str;
  }

  private String[] extractDispositionInfo(String paramString)
    throws IOException
  {
    String[] arrayOfString = new String[4];

    String str1 = paramString;
    paramString = str1.toLowerCase();

    int i = paramString.indexOf("content-disposition: ");
    int j = paramString.indexOf(";");
    if ((i == -1) || (j == -1)) {
      throw new IOException("Content disposition corrupt: " + str1);
    }
    String str2 = paramString.substring(i + 21, j);
    if (!str2.equals("form-data")) {
      throw new IOException("Invalid content disposition: " + str2);
    }

    i = paramString.indexOf("name=\"", j);
    j = paramString.indexOf("\"", i + 7);
    if ((i == -1) || (j == -1)) {
      throw new IOException("Content disposition corrupt: " + str1);
    }
    String str3 = str1.substring(i + 6, j);

    String str4 = null;
    String str5 = null;
    i = paramString.indexOf("filename=\"", j + 2);
    j = paramString.indexOf("\"", i + 10);
    if ((i != -1) && (j != -1)) {
      str4 = str1.substring(i + 10, j);
      str5 = str4;

      int k = Math.max(str4.lastIndexOf('/'), str4.lastIndexOf('\\'));

      if (k > -1) {
        str4 = str4.substring(k + 1);
      }

    }

    arrayOfString[0] = str2;
    arrayOfString[1] = str3;
    arrayOfString[2] = str4;
    arrayOfString[3] = str5;
    return arrayOfString;
  }

  private String extractContentType(String paramString)
    throws IOException
  {
    String str1 = null;

    String str2 = paramString;
    paramString = str2.toLowerCase();

    if (paramString.startsWith("content-type")) {
      int i = paramString.indexOf(" ");
      if (i == -1) {
        throw new IOException("Content type corrupt: " + str2);
      }
      str1 = paramString.substring(i + 1);
    }
    else if (paramString.length() != 0) {
      throw new IOException("Malformed line after disposition: " + str2);
    }

    return str1;
  }

  private String readLine()
    throws IOException
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int i;
    do
    {
      i = this.in.readLine(this.buf, 0, this.buf.length);
      if (i != -1)
        localStringBuffer.append(new String(this.buf, 0, i, this.encoding));
    }
    while (i == this.buf.length);

    if (localStringBuffer.length() == 0) {
      return null;
    }

    int j = localStringBuffer.length();
    if ((j >= 2) && (localStringBuffer.charAt(j - 2) == '\r')) {
      localStringBuffer.setLength(j - 2);
    }
    else if ((j >= 1) && (localStringBuffer.charAt(j - 1) == '\n')) {
      localStringBuffer.setLength(j - 1);
    }
    return localStringBuffer.toString();
  }*/
}