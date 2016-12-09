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
 * ͼƬ�ϴ�
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
	 * �ļ��ϴ� post����
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// ���ò��������ʽ
		request.setCharacterEncoding("gb2312");
		response.setCharacterEncoding("gb2312");
		response.setContentType("text/json;charset=gb2312");
		// �õ������Ӧ����
		PrintWriter out = response.getWriter();
		// ��Ӧ����ֵ
		ResultDto rd = null;
		// ��ȡ�ļ�
		StringBuffer sb = new StringBuffer();
		BufferedReader br = request.getReader();
		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		// ƴ���ļ�url
		StringBuffer sb_fileurl = new StringBuffer();// Ԥ��ֵ�ļ�����
		boolean valBlen=true;//�ļ���֤
		// ��ȡ�ļ�����
		String fileType = request.getParameter("fileType");
		if (!valParam(fileType)) {
			rd = new ResultDto(1001,"�ļ������ʶ���Ϸ�������Ϊ�ջ��߲��ܰ��������ַ���* > | < ? \\  \" : /");
			valBlen=false;
		}
		
		// ��ȡ�ļ�����
		String fileName = request.getParameter("fileName");
		if (!valParam(fileName)) {
			rd = new ResultDto(1002,"�ļ����ֲ��Ϸ�������Ϊ�ջ��߲��ܰ��������ַ���{* > | < ? \\  \" : /}");
			valBlen=false;
		}
		// ��ȡ�ļ���׺
		String suffix = request.getParameter("suffix");
		if (!valParam(suffix)) {
			rd = new ResultDto(1003,"�ļ���׺�����Ϸ�������Ϊ�ջ��߲��ܰ��������ַ���{* > | < ? \\  \" : /}");
			valBlen=false;
		}
		
		if(valBlen){
			//ƴ���ļ�����
			sb_fileurl.append("/").append(fileType).append("/");
			// �õ���ǰʱ��
			sb_fileurl.append(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
					.append("/");
			//ƴ���ļ�����
			sb_fileurl.append(fileName);
			if (suffix.indexOf(".")== -1) {
				sb_fileurl.append(".");
			}
			sb_fileurl.append(suffix);
			System.out.println("�ļ�·����"+ sb_fileurl.toString());
			// ����Base64������������
			try {
				if(decoderBase64File(sb.toString(),rootUrl+sb_fileurl.toString())){
					rd = new ResultDto(200,"�ϴ��ɹ���",sb_fileurl.toString());
				}else{
					rd = new ResultDto(1005,"�ϴ�ʧ�ܣ�");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				System.out.println(" �ļ�д���쳣");
				rd = new ResultDto(1005,"�ϴ�ʧ�ܣ������ļ��Ƿ���base64���룡���������쳣��");
			}
		}
		out.print(JSONObject.fromObject(rd).toString());
	}

	/**
	 * ��base64�ַ����뱣���ļ�
	 * 
	 * @param base64Code
	 * @param targetPath
	 * @throws Exception
	 */

	public static boolean decoderBase64File(String base64Code, String targetPath){
		// base64ת����byte����
		byte[] buffer = Base64.decodeBase64(base64Code);
		try {
			// �õ��ļ������
			FileOutputStream out = new FileOutputStream(targetPath);
			// д���ļ�
			out.write(buffer);
			// �ر������
			out.close();
		} catch (FileNotFoundException e) {//�ļ�·��������
			// �����ļ�·��
			File file=new File(targetPath.substring(0,targetPath.lastIndexOf("/")));
			if(!file.exists()){
				file.mkdirs();
			}
			//���µ���
			decoderBase64File(base64Code, targetPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("д��ʧ�ܣ�");
			return false;
		}
		return true;
	}

	/***
	 * ��֤�����Ƿ�����������
	 * 
	 * @param strval
	 * @return
	 */
	public static boolean valParam(String strval) {
		if (strval == null) {// ��֤�����Ƿ�Ϊnull
			return false;
		}
		//String regEx = "*|>|<|?|\\|\"|:|/"; // ��ʾ@��_
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
