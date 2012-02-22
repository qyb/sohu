/**
 * 
 */
package com.scss.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

/**
 * @author Leon
 * 
 * 
 * Design: 
 * 
 * �ļ���¼����ip��Ȩ�ޣ���Ϊallow���forbid������������order�����ԣ���������ʲô�����ȡ�
 * 
 * ����allow�����ȣ�����allow���¼��ip��ַ����ʹ��forbid�д��ڣ���Ҳ�ᱻ������ʣ�vise versa.
 * 
 * ����IP����ʽ��Ӧ����ͨ�����ʾ��������, 10.7.7.% ���� 10.7.7.0/24 ֮��ġ�
 * 
 * ����IP�����ƣ�Ӧ����ʱ�����ƣ����磬10.7.7.40 ��ipӦ�ñ���ֹ��3���ڷ��� ֮��ġ�
 * 
 *
 */
public class AccessBean {
    
	private static Logger logger = Logger.getLogger(AccessBean.class);
    
    private Lock lock = new ReentrantLock();
	
	public AccessBean() {
		
	}
    
	public AccessBean(String __allow, String __forbid) {
		   allowFileName = __allow;
		   forbidFileName= __forbid;
	}
    
	private int order;
    
	private String allowFileName;
    
	private String forbidFileName;
    
	private HashMap<String, Long> allowList  = new HashMap<String, Long>();
    
	private HashMap<String, Long> forbidList = new HashMap<String, Long>();
    
	public String getAllowFileName() {
		return allowFileName;
	}
    
	public void setAllowFileName(String allowFileName) {
		this.allowFileName = allowFileName;
	}
    
	public String getForbidFileName() {
		return forbidFileName;
	}
    
	public void setForbidFileName(String forbidFileName) {
		this.forbidFileName = forbidFileName;
	}

	public HashMap<String, Long> getAllowList() {
        lock.lock();
		HashMap<String, Long> rtn = null;
        try {
    		rtn = new HashMap<String, Long>(allowList);
        } finally {
        	lock.unlock();
        }
        	
        return rtn;
	}

	public void setAllowList(HashMap<String, Long> allowList) {
		this.allowList = allowList;
	}

	public HashMap<String, Long> getForbidList() {
        lock.lock();
		HashMap<String, Long> rtn = null;
        try {
    		rtn = new HashMap<String, Long>(forbidList);
        } finally {
        	lock.unlock();
        }
        	
        return rtn;
	}

	public void setForbidList(HashMap<String, Long> forbidList) {
		this.forbidList = forbidList;
	}
	
    
	/* *
	 * Reload AllowList ForbidList into memory
	 * *
	 */
	public synchronized boolean reload() {
        FileReader fr = null;
        BufferedReader br = null;
        
        lock.lock();
        
        try {
            allowList.clear();
            forbidList.clear();
            
            try {
                fr = new FileReader(allowFileName);
                br = new BufferedReader(fr);
                
                String line = null;
                while (null != (line = br.readLine())) {
                    if ("" == line) {
                    	continue;
                    }
                    String []fields = line.split("\t");
                    if (2 != fields.length) {
                        logger.error(String.format("read allow list: INVALID [%s]", line));
                    	continue;
                    }
                    
                    allowList.put(fields[0], Long.parseLong(fields[1]));
                }
            } catch (Exception ioe) {
                logger.error(String.format("read allow List Exception: %s", ioe.getMessage()));
                return false;
            } finally {
                if (null != br) {
                	try {
                		br.close();
                	} catch (Exception e) {
                        return false;
    				}
                }
                
                if (null != fr) {
                    try {
                    	fr.close();
                    } catch (Exception ee) {
                        return false;
                    }
                }
            }
            
            try {
                fr = new FileReader(forbidFileName);
                br = new BufferedReader(fr);
                
                String line = null;
                while (null != (line = br.readLine())) {
                    if ("" == line) {
                    	continue;
                    }
                    String []fields = line.split("\t");
                    if (2 != fields.length) {
                        logger.error(String.format("read allow list: INVALID [%s]", line));
                    	continue;
                    }
                    
                    forbidList.put(fields[0], Long.parseLong(fields[1]));
                }
            } catch (Exception ioe) {
                logger.error(String.format("read forbidden List Exception: %s", ioe.getMessage()));
                return false;
            } finally {
                if (null != br) {
                	try {
                		br.close();
                	} catch (Exception e) {
                        return false;
    				}
                }
            	
                if (null != fr) {
                    try {
                    	fr.close();
                    } catch (Exception ee) {
                        return false;
                    }
                }
            }
        	
        } finally {
        	lock.unlock();
        }
        
		return true;
    }

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
    
	
    /**
     * judge specified address in IP dot format whether have privilege
     * to access resource.
     * 
     * need to have expiration limit, this implementation is simple for use first.
     * 
     * @param addr            IP format as: 8.8.8.8 
     * @return
     * 
     * true                    pass
     * false                   forbidden
     * 
     */
	public boolean judgeAccess(String addr) {
        if (addr == null) return false;
        addr = addr.trim();
        
        logger.debug(String.format("===========�� ClientAddress: [%s] under check now.", addr));
        
        if (Constant.ALLOWFIRST == order) {
        	if (allowList.keySet().contains(addr)) {
        		return true;
        	}
            
        	if (forbidList.keySet().contains(addr)) {
        		return false;
        	}
            
        } else if (Constant.FORBIDFIRST == order) {
        	if (forbidList.keySet().contains(addr)) {
        		return false;
        	}
            
        	if (allowList.keySet().contains(addr)) {
        		return true;
        	}
        }
		
        return true;
	}
}




