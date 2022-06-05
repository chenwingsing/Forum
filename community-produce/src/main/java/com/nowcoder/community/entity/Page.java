package com.nowcoder.community.entity;
//封装分页相关的信息
public class Page {
    //当前的页码
    private int current = 1;

    //显示上限
    private int limit = 10;

    //数据总数（用于计算总页数）
    private int rows;

    //查询路径(用于复用分页链接)
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if(current >= 1) {//单独增加一个判断，避免用户输入负数
        this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit >= 1 && limit <= 100) {//避免用户乱设置，而且100以内对服务器压力也不太大，还有一个就是避免被爬虫爬死
        this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if(rows >= 0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    //下面都是额外增加的方法

    // 获取当前页的起始行
    public int getOffset() {
        //current * limit -limit
        return (current - 1) * limit;
    }

     //获取总页数
    public int getTotal() {
        // rows / limit [+1]
        if(rows % limit == 0) {
            return rows / limit;
        } else {
            return rows / limit + 1;//有余数就代表还要多一页
        }
    }

    //获取起始页码
    //因为我们不可能在一页显示全部页码，可以显示本页的前两页和后两页
    public int getFrom() {
        int from = current - 2;
        return from < 1 ? 1 :from; //如果当前页是1，那上面一条就变成负数，所以这里增加一个判断，如果小于1就从1开始，否则就是原本的from值
    }

    public int getTo() {
        int to = current + 2;
        int total = getTotal();
        return to > total ? total : to;//如果大于总页数就显示total，否则显示to
    }
}
