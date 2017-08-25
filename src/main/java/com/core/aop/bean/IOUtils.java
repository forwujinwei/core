/*  1:   */ package com.core.aop.bean;
/*  2:   */ 
/*  3:   */ import java.io.ByteArrayOutputStream;
/*  4:   */ import java.io.IOException;
/*  5:   */ import java.io.InputStream;
/*  6:   */ import java.io.OutputStream;
/*  7:   */ 
/*  8:   */ public class IOUtils
/*  9:   */ {
/* 10:   */   private static final int DEFAULT_BUFFERSIZE = 4096;
/* 11:   */   private static final int DEFAULT_SLEEP_COUNT = 3;
/* 12:   */   
/* 13:   */   public static long copy(InputStream input, OutputStream output)
/* 14:   */     throws IOException
/* 15:   */   {
/* 16:20 */     long count = 0L;
/* 17:21 */     if ((input == null) || (output == null)) {
/* 18:22 */       return count;
/* 19:   */     }
/* 20:24 */     byte[] buffer = new byte[4096];
/* 21:25 */     int n = 0;
/* 22:   */     for (;;)
/* 23:   */     {
/* 24:27 */       int read = input.read(buffer);
/* 25:28 */       if (read < 0) {
/* 26:   */         break;
/* 27:   */       }
/* 28:31 */       output.write(buffer, 0, read);
/* 29:32 */       count += read;
/* 30:33 */       output.flush();
/* 31:34 */       n++;
/* 32:35 */       if (n % 3 == 0) {
/* 33:   */         try
/* 34:   */         {
/* 35:37 */           Thread.sleep(0L);
/* 36:   */         }
/* 37:   */         catch (InterruptedException localInterruptedException) {}
/* 38:   */       }
/* 39:   */     }
/* 40:43 */     return count;
/* 41:   */   }
/* 42:   */   
/* 43:   */   public static final byte[] getByteArray(InputStream in)
/* 44:   */   {
/* 45:47 */     ByteArrayOutputStream out = null;
/* 46:   */     try
/* 47:   */     {
/* 48:49 */       out = new ByteArrayOutputStream();
/* 49:50 */       copy(in, out);
/* 50:51 */       byte[] b = out.toByteArray();
/* 51:52 */       return b;
/* 52:   */     }
/* 53:   */     catch (IOException e)
/* 54:   */     {
/* 55:54 */       throw new RuntimeException(e.getMessage(), e);
/* 56:   */     }
/* 57:   */     finally
/* 58:   */     {
/* 59:56 */       if (out != null) {
/* 60:   */         try
/* 61:   */         {
/* 62:58 */           out.close();
/* 63:   */         }
/* 64:   */         catch (IOException localIOException2) {}
/* 65:   */       }
/* 66:   */     }
/* 67:   */   }
/* 68:   */ }


/* Location:           C:\Users\wujinwei\.m2\repository\terran4j\terran4j-commons-util\1.0.0\terran4j-commons-util-1.0.0.jar
 * Qualified Name:     com.terran4j.commons.util.IOUtils
 * JD-Core Version:    0.7.0.1
 */