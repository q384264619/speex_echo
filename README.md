# speex_echo
speexdsp编译例子，及本地测试例子

# 注意
  本工程中，是本地测试，本地效果不表示实际使用效果，例子只是为了演示speexdsp调用的方式方法

```java
 final  byte[] temp = Arrays.copyOf(data,len);
               final  int size = len;
               //模拟300ms延时
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        audioDevice.write(temp,0,size);
                    }
                },300);
                
```
是为了测试模拟网络延时。
