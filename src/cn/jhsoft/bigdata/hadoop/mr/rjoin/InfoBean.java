package cn.jhsoft.bigdata.hadoop.mr.rjoin;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * 订单表：
 * id     date         pid   amount
 * 1001   20150710     1000     2
 *
 * 商品表：
 * id      name       category_id   price
 * 1000    苹果7       1             5999
 *
 * Created by chen on 2017/7/15.
 */
public class InfoBean implements Writable {

    // 订单
    private int id;
    private String dateString;
    private  String pid;
    private int amount;

    // 商品
    private String name;
    private int category_id;
    private float price;

    // flag=0表示这个对象封装的是订单表记录
    // flag=1表示这个对象封装的是产品信息记录
    private String flag;

    public InfoBean() {
    }

    public InfoBean(int id, String dateString, String pid, int amount, String name, int category_id, float price, String flag) {
        this.id = id;
        this.dateString = dateString;
        this.pid = pid;
        this.amount = amount;
        this.name = name;
        this.category_id = category_id;
        this.price = price;
        this.flag = flag;
    }

    public void set(int id, String dateString, String pid, int amount, String name, int category_id, float price, String flag) {
        this.id = id;
        this.dateString = dateString;
        this.pid = pid;
        this.amount = amount;
        this.name = name;
        this.category_id = category_id;
        this.price = price;
        this.flag = flag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(id);
        dataOutput.writeUTF(dateString);
        dataOutput.writeUTF(pid);
        dataOutput.writeInt(amount);
        dataOutput.writeUTF(name);
        dataOutput.writeInt(category_id);
        dataOutput.writeFloat(price);
        dataOutput.writeUTF(flag);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.id = dataInput.readInt();
        this.dateString = dataInput.readUTF();
        this.pid = dataInput.readUTF();
        this.amount = dataInput.readInt();
        this.name = dataInput.readUTF();
        this.category_id = dataInput.readInt();
        this.price = dataInput.readFloat();
        this.flag = dataInput.readUTF();

    }

    @Override
    public String toString() {
        return "id=" + id +
                ", dateString='" + dateString + '\'' +
                ", pid=" + pid +
                ", amount=" + amount +
                ", name='" + name + '\'' +
                ", category_id=" + category_id +
                ", price=" + price;
    }
}
