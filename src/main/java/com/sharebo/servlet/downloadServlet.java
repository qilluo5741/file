package com.sharebo.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.codec.binary.Base64;

/**
 * 文件下载
 */
@WebServlet("/download")
public class downloadServlet extends HttpServlet {
	private static final String rootUrl = "C://fileupload";
	private static final long serialVersionUID = 1L;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public downloadServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * 文件上传 post方法
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// 设置参数编码格式
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		// 得到路径
		String fileUri = request.getParameter("fileUri");
		// 得到返回类型 base64/流 默认为流
		String restype = request.getParameter("restype");// 两个值：1：文件类型：图片返回base64
		if (restype != null && restype.equals("base64")) {
			response.setContentType("text/json;charset=utf-8");
			String base64Str=encodeBase64File(rootUrl+fileUri);
			PrintWriter out=response.getWriter();
			out.print("{\"res\":\""+"data:image/png;base64,"+base64Str+"\"}");
			out.flush();
			out.close();
		} else {
			response.setContentType("application/octet-stream");
			// response.setHeader("Content-Disposition","attachment;filename=\""+fileUri.substring(fileUri.lastIndexOf("/")+1,fileUri.length())+"\"");
			response.addHeader("Content-Disposition","attachment;filename="+ new String(fileUri.substring(fileUri.lastIndexOf("/") + 1,fileUri.length()).getBytes("GBK"),"ISO8859_1"));
			// 读取文件
			InputStream is = null;
			OutputStream os = null;
			try {
				is = new BufferedInputStream(new FileInputStream(rootUrl
						+ fileUri));
				// 定义输出字节流
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				// 定义response的输出流
				os = new BufferedOutputStream(response.getOutputStream());
				// 定义buffer
				byte[] buffer = new byte[4 * 1024]; // 4k Buffer
				int read = 0;
				// 从文件中读入数据并写到输出字节流中
				while ((read = is.read(buffer)) != -1) {
					baos.write(buffer, 0, read);
				}
				// 将输出字节流写到response的输出流中
				os.write(baos.toByteArray());
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				// 关闭输出字节流和response输出流
				if (os != null) {
					os.close();
				}
				if (is != null) {
					is.close();
				}
			}
		}
	}

	/**
	 * 将文件转成base64 字符串
	 * @param path文件路径
	 * @return *
	 * @throws Exception
	 */
	public static String encodeBase64File(String path) throws IOException {
		File file = new File(path);
		FileInputStream inputFile = new FileInputStream(file);
		byte[] buffer = new byte[(int) file.length()];
		inputFile.read(buffer);
		inputFile.close();
		return Base64.encodeBase64String(buffer);
	}
	/*
	public static void main(String[] args) {
		int [] array={1,2,3,4,5,6};
		int[] resarray=rev(array, 1, 4);
		for(int i=0;i<resarray.length;i++){
			System.out.print("  "+resarray[i]);			
		}
	}
	//进行下标反序
	public static int []  rev(int [] array,int begin,int end){
		int c=begin+end;
		for(int i=0;i<array.length;i++){
			if(i>=begin&&i<=end/2){
				//更换顺序
				int temp;
				//得到开始值
				temp=array[i];
				//更换开始值
				array[i]=array[c-i];
				//更换结束值
				array[c-i]=temp;
			}
		}
		return array;
	}*/
}
