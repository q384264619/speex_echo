package com.vnetoo.speex.echo.device;

public class CirCleBufer {

    private static boolean bEmpty = true;
    private static boolean bFull = false;

    private static int nBufSize = 1024 * 128;
    private static byte[] pBuf = new byte[nBufSize];
    private static int nReadPos = 0;
    private static int nWritePos = 0;


    /*
     * 函数介绍 向缓冲区写入数据，返回实际写入的字节数
     */
    public static int write(byte[] buf, int count) {
        if (count <= 0)
            return 0;
        bEmpty = false;
        // 缓冲区已满，不能继续写入
        if (bFull) {
            return 0;
        } else if (nReadPos == nWritePos) // 缓冲区为空时
        {
 /*    == 内存模型 == 
  (empty)  m_nReadPos  (empty)   
 |----------------------------------|-----------------------------------------| 
     m_nWritePos m_nBufSize 
 */
            int leftcount = nBufSize - nWritePos;
            if (leftcount > count) {
                System.arraycopy(buf, 0, pBuf, nWritePos, count);
                nWritePos += count;
                bFull = (nWritePos == nReadPos);
                return count;
            } else {
                System.arraycopy(buf, 0, pBuf, nWritePos, leftcount);
                nWritePos = (nReadPos > count - leftcount) ? count - leftcount : nWritePos;
                System.arraycopy(buf, leftcount, pBuf, 0, nWritePos);
                bFull = (nWritePos == nReadPos);
                return leftcount + nWritePos;
            }
        } else if (nReadPos < nWritePos) // 有剩余空间可写入
        {
 /*    == 内存模型 == 
  (empty)   (data)   (empty) 
 |-------------------|----------------------------|---------------------------| 
   m_nReadPos  m_nWritePos (leftcount)  
 */
            // 剩余缓冲区大小(从写入位置到缓冲区尾)

            int leftcount = nBufSize - nWritePos;
            if (leftcount > count) // 有足够的剩余空间存放
            {
                System.arraycopy(buf, 0, pBuf, nWritePos, count);
                nWritePos += count;
                bFull = (nReadPos == nWritePos);
                return count;
            } else // 剩余空间不足
            {
                // 先填充满剩余空间，再回头找空间存放
                System.arraycopy(buf, 0, pBuf, nWritePos, leftcount);
                nWritePos = (nReadPos >= count - leftcount) ? count - leftcount : nReadPos;
                System.arraycopy(buf, leftcount, pBuf, 0, nWritePos);
                bFull = (nReadPos == nWritePos);
                return leftcount + nWritePos;
            }
        } else {
 /*    == 内存模型 == 
  (unread)   (read)   (unread) 
 |-------------------|----------------------------|---------------------------| 
   m_nWritePos (leftcount) m_nReadPos   
 */
            int leftcount = nReadPos - nWritePos;
            if (leftcount > count) {
                // 有足够的剩余空间存放
                System.arraycopy(buf, 0, pBuf, nWritePos, count);
                nWritePos += count;
                bFull = (nReadPos == nWritePos);
                return count;
            } else {
                // 剩余空间不足时要丢弃后面的数据
                System.arraycopy(buf, 0, pBuf, nWritePos, leftcount);
                nWritePos += leftcount;
                bFull = (nReadPos == nWritePos);
                return leftcount;
            }
        }
    }

    /*
     * 函数介绍 从缓冲区读数据，返回实际读取的字节数
     */
    public static int read(byte[] buf, int count) {
        if (count <= 0)
            return 0;
        bFull = false;
        if (bEmpty) // 缓冲区空，不能继续读取数据
        {
            return 0;
        } else if (nReadPos == nWritePos) // 缓冲区满时
        {
 /*    == 内存模型 == 
 (data)  m_nReadPos  (data) 
 |--------------------------------|--------------------------------------------| 
  m_nWritePos  m_nBufSize 
 */
            int leftcount = nBufSize - nReadPos;
            if (leftcount > count) {
                System.arraycopy(pBuf, nReadPos, buf, 0, count);
                nReadPos += count;
                bEmpty = (nReadPos == nWritePos);
                return count;
            } else {
                System.arraycopy(pBuf, nReadPos, buf, 0, count);
                nReadPos = (nWritePos > count - leftcount) ? count - leftcount : nWritePos;
                System.arraycopy(pBuf, 0, buf, leftcount, nReadPos);
                bEmpty = (nReadPos == nWritePos);
                return leftcount + nReadPos;
            }
        } else if (nReadPos < nWritePos) // 写指针在前(未读数据是连接的)
        {
 /*    == 内存模型 == 
  (read)   (unread)   (read) 
 |-------------------|----------------------------|---------------------------| 
   m_nReadPos  m_nWritePos   m_nBufSize 
 */
            int leftcount = nWritePos - nReadPos;
            int c = (leftcount > count) ? count : leftcount;
            System.arraycopy(pBuf, nReadPos, buf, 0, c);
            nReadPos += c;
            bEmpty = (nReadPos == nWritePos);
            return c;
        } else  // 读指针在前(未读数据可能是不连接的)
        {
 /*    == 内存模型 == 
  (unread)  (read)   (unread) 
 |-------------------|----------------------------|---------------------------| 
   m_nWritePos   m_nReadPos   m_nBufSize 

 */
            int leftcount = nBufSize - nReadPos;
            if (leftcount > count) // 未读缓冲区够大，直接读取数据
            {
                System.arraycopy(pBuf, nReadPos, buf, 0, count);
                nReadPos += count;
                bEmpty = (nReadPos == nWritePos);
                return count;
            } else // 未读缓冲区不足，需回到缓冲区头开始读
            {
                System.arraycopy(pBuf, nReadPos, buf, 0, leftcount);
                nReadPos = (nWritePos >= count - leftcount) ? count - leftcount : nWritePos;
                System.arraycopy(pBuf, 0, buf, leftcount, nReadPos);
                bEmpty = (nReadPos == nWritePos);
                return leftcount + nReadPos;
            }
        }
    }

    public static boolean isFull() {
        return bFull;
    }

    public static boolean isEmpty() {
        return bEmpty;
    }

    public static void setEmpty() {
        nReadPos = 0;
        nWritePos = 0;
        bEmpty = true;
        bFull = false;
    }

    /**
     * 获取缓冲区有效数据长度
     */
    public static int getUsedSize() {
        if (bEmpty) {
            return 0;
        } else if (bFull) {
            return nBufSize;
        } else if (nReadPos < nWritePos) {
            return nWritePos - nReadPos;
        } else {
            return nBufSize - nReadPos + nWritePos;
        }
    }

    /**
     * 获取缓冲区空闲空间数据长度
     */
    public static int getFreeSize() {
        if (bEmpty) {
            return nBufSize;
        } else if (bFull) {
            return 0;
        } else if (nReadPos > nWritePos) {
            return nReadPos - nWritePos;
        } else {
            return nBufSize - nWritePos + nReadPos;
        }
    }

}
