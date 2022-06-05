package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换符
    private static final String REPLACEMENT = "***";

    //根节点
    private TrieNode rootNode = new TrieNode();

    @PostConstruct//这是一个初始化方法，当容器实例化这个bean后，在调用构造器后，这个方法被自动调用
    public void init() {
        try(//在try-catch中写会自动关闭流
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));//转成缓冲效率高
                ){
            String keyword;
            while((keyword = reader.readLine()) != null) {
                //添加到前缀树
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词失败:"+ e.getMessage());
        }
    }

    //将一个敏感词添加到前缀树
    private void addKeyword(String keyword) {
        TrieNode tempNode = rootNode;
        for(int i = 0; i <keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if (subNode == null) {
                //初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }
            //指向子节点，进入下一轮循环
            tempNode = subNode;
            //设置结束标识
            if(i == keyword.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }
    }

    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        // 指针1
        TrieNode tempNode = rootNode;
        // 指针2
        int begin = 0;
        // 指针3
        int position = 0;
        // 结果
        StringBuilder sb = new StringBuilder();

        while(begin < text.length()){
            if(position < text.length()) {
                Character c = text.charAt(position);

                // 跳过符号 因为有些人写成 ☆赌☆博 来逃避识别
                if (isSymbol(c)) {
                    if (tempNode == rootNode) {
                        begin++;
                        sb.append(c);
                    }
                    position++;
                    continue;
                }//遇到符号直接跳过全部，不进去里面了

                // 检查下级节点
                tempNode = tempNode.getSubNode(c);
                if (tempNode == null) {
                    // 以begin开头的字符串不是敏感词
                    sb.append(text.charAt(begin));
                    // 进入下一个位置
                    position = ++begin;
                    // 重新指向根节点
                    tempNode = rootNode;
                }
                // 发现敏感词
                else if (tempNode.isKeywordEnd()) {
                    sb.append(REPLACEMENT);
                    begin = ++position;
                    tempNode = rootNode;
                }
                // 检查下一个字符
                else {//这个情况就是下级结点和我们的字符匹配到了，然后移动position，然后begin先不动
                    position++;
                }
            }
            // position遍历越界仍未匹配到敏感词
            else{
                sb.append(text.charAt(begin));
                position = ++begin;
                tempNode = rootNode;//别忘了这句 很关键的 重新从前缀树找 取消这句可以用"赌博吸毒***"做实验测试
            }
        }
        return sb.toString();
    }
    /*下面这个好像有错误逻辑，和上面那个主要区别是用指针2作为循环条件，具体原因看https://www.nowcoder.com/study/live/246/3/1讨论区

     * 过滤敏感词，这是一个公共方法
     * @param text 待过滤的文本
     * @return 过滤后的文本

    public String filter(String text) {
        if(StringUtils.isBlank(text)) {
            return null;
        }
        //指针1
        TrieNode tempNode = rootNode;
        //指针2
        int begin = 0;
        //指针3
        int position = 0;
        //结果
        StringBuilder sb = new StringBuilder();
        while(position < text.length()) {
            char c = text.charAt(position);
            //跳过符号 因为有些人写成 ☆赌☆博 来逃避识别
            if(isSymbol(c)) {
                //若指针1处于根节点,将此符号计入结果，让指针2向下走一步
                if(tempNode == rootNode) {
                    sb.append(c);
                    begin++;
                }
                // 无论在开头还是中间，指针3都向下走一步。自己推理下为什么指针3要在外面加。
                position++;
                continue;
            }
            //检查下级节点
            tempNode = tempNode.getSubNode(c);
            if(tempNode == null) {
                //以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                //进入下一个位置
                position = ++begin;
                //重新指向根节点
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd()) {
                //发现敏感词 将beigin到position字符串替换掉
                sb.append(REPLACEMENT);
                //进入下一个位置
                begin = ++position;
                //重新指向根节点
                tempNode = rootNode;
            } else {
                //检查下一个字符
                position++;
            }
        }
         //将最后一批字符计入结果
        sb.append(text.substring(begin));
        return sb.toString();
    }
*/
    //判断是否为符号
    private boolean isSymbol(Character c) {
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);//判断是否合法的字符，也就是判断是否为普通字符，取反之后特殊符号就为true
    }// 0x2E80 到 0x9FFF东亚文字范围，我们认为是合法的，所以除去这个区间

    //前缀树
    private class TrieNode {
        //关键词结束标志
        private boolean  isKeywordEnd = false;
        //子节点(key是下级字符，value是下级节点)
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }
        //添加子节点
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        //获取子节点
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }


    }
}
