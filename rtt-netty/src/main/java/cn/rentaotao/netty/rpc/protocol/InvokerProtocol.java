package cn.rentaotao.netty.rpc.protocol;


import java.io.Serializable;
import java.util.Arrays;

/**
 * 自定义传输协议
 *
 * @author rtt
 * @date 2023/2/2 10:16
 */
public class InvokerProtocol implements Serializable {

    private String className;

    private String methodName;

    private Class<?>[] params;

    private Object[] values;

    public InvokerProtocol(String className, String methodName, Class<?>[] params, Object[] values) {
        this.className = className;
        this.methodName = methodName;
        this.params = params;
        this.values = values;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParams() {
        return params;
    }

    public void setParams(Class<?>[] params) {
        this.params = params;
    }

    public Object[] getValues() {
        return values;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "InvokerProtocol{" +
                "className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", params=" + Arrays.toString(params) +
                ", values=" + Arrays.toString(values) +
                '}';
    }
}
