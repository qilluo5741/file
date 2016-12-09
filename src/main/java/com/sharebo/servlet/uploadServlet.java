package com.sharebo.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import org.apache.tomcat.util.codec.binary.Base64;

import com.sharebo.util.ResultDto;

/**
 * 图片上传
 */
@WebServlet("/upload")
public class uploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String rootUrl="C://fileupload";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public uploadServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

	}

	/**
	 * 文件上传 post方法
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// 设置参数编码格式
		request.setCharacterEncoding("gb2312");
		response.setCharacterEncoding("gb2312");
		response.setContentType("text/json;charset=gb2312");
		// 得到输出响应对象
		PrintWriter out = response.getWriter();
		// 响应返回值
		ResultDto rd = null;
		// 获取文件
		StringBuffer sb = new StringBuffer();
		BufferedReader br = request.getReader();
		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		// 拼接文件url
		StringBuffer sb_fileurl = new StringBuffer();// 预设值文件磁盘
		boolean valBlen=true;//文件验证
		// 获取文件归类
		String fileType = request.getParameter("fileType");
		if (!valParam(fileType)) {
			rd = new ResultDto(1001,"文件归类标识不合法！不能为空或者不能包含以下字符：* > | < ? \\  \" : /");
			valBlen=false;
		}
		
		// 获取文件名字
		String fileName = request.getParameter("fileName");
		if (!valParam(fileName)) {
			rd = new ResultDto(1002,"文件名字不合法！不能为空或者不能包含以下字符：{* > | < ? \\  \" : /}");
			valBlen=false;
		}
		// 获取文件后缀
		String suffix = request.getParameter("suffix");
		if (!valParam(suffix)) {
			rd = new ResultDto(1003,"文件后缀名不合法！不能为空或者不能包含以下字符：{* > | < ? \\  \" : /}");
			valBlen=false;
		}
		
		if(valBlen){
			//拼接文件归类
			sb_fileurl.append("/").append(fileType).append("/");
			// 得到当前时间
			sb_fileurl.append(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
					.append("/");
			//拼接文件名字
			sb_fileurl.append(fileName);
			if (suffix.indexOf(".")== -1) {
				sb_fileurl.append(".");
			}
			sb_fileurl.append(suffix);
			System.out.println("文件路径："+ sb_fileurl.toString());
			// 根据Base64解析成输入流
			try {
				if(decoderBase64File(sb.toString(),rootUrl+sb_fileurl.toString())){
					rd = new ResultDto(200,"上传成功！",sb_fileurl.toString());
				}else{
					rd = new ResultDto(1005,"上传失败！");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				System.out.println(" 文件写入异常");
				rd = new ResultDto(1005,"上传失败！请检查文件是否是base64编码！或者其他异常！");
			}
		}
		out.print(JSONObject.fromObject(rd).toString());
	}

	/**
	 * 将base64字符解码保存文件
	 * 
	 * @param base64Code
	 * @param targetPath
	 * @throws Exception
	 */

	public static boolean decoderBase64File(String base64Code, String targetPath){
		// base64转换成byte数组
		byte[] buffer = Base64.decodeBase64(base64Code);
		try {
			// 得到文件输出流
			FileOutputStream out = new FileOutputStream(targetPath);
			// 写入文件
			out.write(buffer);
			// 关闭输出流
			out.close();
		} catch (FileNotFoundException e) {//文件路径不存在
			// 创建文件路径
			File file=new File(targetPath.substring(0,targetPath.lastIndexOf("/")));
			if(!file.exists()){
				file.mkdirs();
			}
			//重新调用
			decoderBase64File(base64Code, targetPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("写入失败！");
			return false;
		}
		return true;
	}

	/***
	 * 验证参数是否包含特殊符号
	 * 
	 * @param strval
	 * @return
	 */
	public static boolean valParam(String strval) {
		if (strval == null) {// 验证参数是否为null
			return false;
		}
		//String regEx = "*|>|<|?|\\|\"|:|/"; // 表示@或_
		if(strval.indexOf("*")!=-1){return false;}
		if(strval.indexOf("|")!=-1){return false;}
		if(strval.indexOf(">")!=-1){return false;}
		if(strval.indexOf("<")!=-1){return false;}
		if(strval.indexOf("?")!=-1){return false;}
		if(strval.indexOf("\\")!=-1){return false;}
		if(strval.indexOf("/")!=-1){return false;}
		if(strval.indexOf(":")!=-1){return false;}
		if(strval.indexOf("\"")!=-1){return false;}
		return true;
	}
}
