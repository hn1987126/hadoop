package cn.jhsoft.bigdata.hadoop.mr.secondarysort;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

/**
 * 订单实体Bean
 */
public class OrderBean implements WritableComparable<OrderBean>{

	private Text itemid;
	private DoubleWritable amount;

	public OrderBean() {
	}

	public OrderBean(Text itemid, DoubleWritable amount) {
		set(itemid, amount);

	}

	public void set(Text itemid, DoubleWritable amount) {

		this.itemid = itemid;
		this.amount = amount;

	}



	public Text getItemid() {
		return itemid;
	}

	public DoubleWritable getAmount() {
		return amount;
	}



	// 这里只是告诉别人怎么比大小，并不自己排，在后面map task合并成reduces的时候，他们合并的时候会用到这个规则去排序
	// 先按订单顺序排，再按金额倒序排
	// 倒序返回-1，顺序返回1
	@Override
	public int compareTo(OrderBean o) {

		// compareTo 方法，前面比后面大则返回-1，前面比后面小返回1，相等返回0
		// 先看订单，顺序要1，我(this)比你大，返回1
		int cmp = this.itemid.compareTo(o.getItemid());

		// 订单号相同的，再按金额倒序。前面加-  是取反
		if (cmp == 0) {
			cmp = -this.amount.compareTo(o.getAmount());
		}
		return cmp;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(itemid.toString());
		out.writeDouble(amount.get());
		
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		String readUTF = in.readUTF();
		double readDouble = in.readDouble();
		
		this.itemid = new Text(readUTF);
		this.amount= new DoubleWritable(readDouble);
	}


	@Override
	public String toString() {

		return itemid.toString() + "\t" + amount.get();
		
	}

}
