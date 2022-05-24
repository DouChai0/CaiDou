/*****************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one                *
 * or more contributor license agreements.  See the NOTICE file              *
 * distributed with this work for additional information                     *
 * regarding copyright ownership.  The ASF licenses this file                *
 * to you under the Apache License, Version 2.0 (the                         *
 * "License"); you may not use this file except in compliance                *
 * with the License.  You may obtain a copy of the License at                *
 *                                                                           *
 *     http://www.apache.org/licenses/LICENSE-2.0                            *
 *                                                                           *
 * Unless required by applicable law or agreed to in writing,                *
 * software distributed under the License is distributed on an               *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY                    *
 * KIND, either express or implied.  See the License for the                 *
 * specific language governing permissions and limitations                   *
 * under the License.                                                        *
 *                                                                           *
 *                                                                           *
 * This file is part of the BeanShell Java Scripting distribution.           *
 * Documentation and updates may be found at http://www.beanshell.org/       *
 * Patrick Niemeyer (pat@pat.net)                                            *
 * Author of Learning Java, O'Reilly & Associates                            *
 *                                                                           *
 *****************************************************************************/



package bsh;

import com.ldq.Utils.Utils;

/**
	EvalError indicates that we cannot continue evaluating the script
	or the script has thrown an exception.

	EvalError may be thrown for a script syntax error, an evaluation 
	error such as referring to an undefined variable, an internal error.
	<p>
	
	@see TargetError
*/
public class EvalError extends Exception 
{
	SimpleNode node;

	// Note: no way to mutate the Throwable message, must maintain our own
	String message;

	CallStack callstack;

	public EvalError(String s, SimpleNode node, CallStack callstack ) {
		setMessage(s);
		this.node = node;
		// freeze the callstack for the stack trace.
		if ( callstack != null )
			this.callstack = callstack.copy();
	}

	/**
		Print the error with line number and stack trace.
	*/
	public String toString() 
	{
		String trace;
		if ( node != null )
			trace = "\n\n堆栈Trace:\n行数: "+ node.getLineNumber()
				+ " : 文件位置: "+ node.getSourceFile()
				+ " : "+node.getText();
		else
			// Users should not normally see this.
			trace = ": <未知位置>";

		if ( callstack != null )
			trace = trace +"\n" + getScriptStackTrace();

		return getMessage() + trace;
	}

	/**
		Re-throw the error, prepending the specified message.
	*/
	public void reThrow( String msg ) 
		throws EvalError
	{
		prependMessage( msg );
		throw this;
	}

	/**
		The error has trace info associated with it. 
		i.e. It has an AST node that can print its location and source text.
	*/
	SimpleNode getNode() {
		return node;
	}

	void setNode( SimpleNode node ) {
		this.node = node;
	}

	public String getErrorText() { 
		if ( node != null )
			return node.getText() ;
		else
			return "<unknown error>";
	}

	public int getErrorLineNumber() { 
		if ( node != null )
			return node.getLineNumber() ;
		else
			return -1;
	}

	public String getErrorSourceFile() {
		if ( node != null )
			return node.getSourceFile() ;
		else
			return "<unknown file>";
	}
	public String Get_Raw_Infos(){
		return "\n---------------------------------------\n"+"时间:"+ Utils.GetNowTime()+"\n"+CollectInfos() + "\n------------------------------------------------";
	}
	public String Print_Crash_Infos(){
		String CrashInfo = "";
		CrashInfo = CrashInfo + "异常:"+ getMessage();
		CrashInfo = CrashInfo + "\n可能存在的文件:" + (node==null?"未知文件":node.getSourceFile())+":第"+(node==null?"0":node.getLineNumber())+"行";
		CrashInfo = CrashInfo + "\n方法链:"+GetCallInfoTrain()+"\n";
		return CrashInfo;
	}
	public String CollectInfos(){
		String Raw = Print_Crash_Infos();
		Throwable th = getCause();
		while (th !=null)
		{
			if(th instanceof EvalError)
			{
				Raw = Raw + "\n\n ↓ 上层Throwable ↓\n\n" + ((EvalError)th).Print_Crash_Infos();
			}else
			{
				Raw = Raw + "\n\n ↓ 上层Throwable ↓\n\n" + th.toString();
			}
			th = th.getCause();
		}
		return Raw;
	}
	public String GetCallInfoTrain(){
		String Node = "";
		String FileInfo = "("+(node ==null ? "Unknow" : node.getSourceFileName())+":"+(node ==null ? "0" : node.getLineNumber())+")";
		if(callstack ==null)return "Unknow"+FileInfo;
		CallStack stack = callstack.copy();

		while ( stack.depth() > 0 )
		{
			NameSpace ns = stack.pop();
			SimpleNode node = ns.getNode();
			while (ns!=null)
			{
				if(ns.isMethod)
				{
					Node = Node + "\n"+ns.getName()+"("
							+(node==null ? "Unknow":node.getSourceFileName())+":"+(node==null?"0":node.getLineNumber())+")";
				}
				ns = ns.getParent();

			}
		}
		return Node;

	}
	public String getCallStackTraceInfos(){
		if ( callstack == null )
			return "<Unknown>";
		String sBefore = "";

		return "";
	}
	public String getScriptStackTrace() 
	{
		if ( callstack == null )
			return "<Unknown>";

		String trace = "";
		//Top Callstack


		CallStack stack = callstack.copy();
		while ( stack.depth() > 0 ) 
		{
			NameSpace ns = stack.pop();
			SimpleNode node = ns.getNode();
			if ( ns.isMethod )
			{
				trace = trace + "\n被此方法调用: " + ns.getName();
				if ( node != null )
					trace += " : 行数: "+ node.getLineNumber()
						+ " : 位于文件: "+ node.getSourceFile()
						+ " : "+node.getText();
			}
		}

		return trace;
	}

	/**
		@see #toString() for a full display of the information
	*/
	public String getMessage() { return message; }

	public void setMessage( String s ) { message = s; }

	/**
		Prepend the message if it is non-null.
	*/
	protected void prependMessage( String s ) 
	{ 
		if ( s == null )
			return;

		if ( message == null )
			message = s;
		else
			message = s + " : "+ message;
	}

}

