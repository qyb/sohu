/**
 * 
 */
package com.scss.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;

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
	
	public AccessBean() {
		
	}
    
	public AccessBean(String __allow, String __forbid) {
		   allowFileName = __allow;
		   forbidFileName= __forbid;
	}
    
	private int order;
    
	private String allowFileName;
    
	private String forbidFileName;
    
	private Hashtable<String, Long> allowList  = new Hashtable<String, Long>();
	private Hashtable<String, Long> allowList_shadow  = new Hashtable<String, Long>();
	
    
	private Hashtable<String, Long> forbidList = new Hashtable<String, Long>();
	private Hashtable<String, Long> forbidList_shadow = new Hashtable<String, Long>();
    
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

	public Hashtable<String, Long> getAllowList() {
		return allowList;
	}

	public void setAllowList(Hashtable<String, Long> allowList) {
		this.allowList = allowList;
	}

	public Hashtable<String, Long> getForbidList() {
		return forbidList;
	}

	public void setForbidList(Hashtable<String, Long> forbidList) {
		this.forbidList = forbidList;
	}
    
	/* *
	 * Reload AllowList ForbidList into memory
	 * *
	 */
	public boolean reload() {
        FileReader fr		= null;
        BufferedReader br	= null;
        FileWriter wr		= null;
        
        {
            allowList_shadow.clear();
            forbidList_shadow.clear();
            
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
                    
                    // �жϳ�ʱʱ���Ƿ��Ѿ�С�ڵ��ڵ�ǰʱ�䣬��true����Ӧ��ɾ��������Ϣ
                    try {
	                    Long expire = Long.parseLong(fields[1]);
	                    Date date = new Date(expire.longValue());
	                    Date now  = new Date();
	                    if (expire == 0 || date.compareTo(now) > 0) {
	                    	allowList_shadow.put(fields[0], expire);
	                    }
                    } catch (Exception ee) {
                    	logger.error(String.format("Date Convertion Error: [%s]", line));
                    	continue;
                    }
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
            	wr = new FileWriter(this.allowFileName, false);
            	
            	for (String ip : allowList_shadow.keySet()) {
            		wr.write(ip + "\t" + allowList_shadow.get(ip) + "\n");
            	}
            } catch (Exception e) {
            	logger.fatal("Fill the ip back failed.");
            } finally {
            	if (null != wr) {
            		try {
						wr.close();
					} catch (IOException e) { }
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
                    
                    // �жϳ�ʱʱ���Ƿ��Ѿ�С�ڵ��ڵ�ǰʱ�䣬��true����Ӧ��ɾ��������Ϣ
                    try {
	                    Long expire = Long.parseLong(fields[1]);
	                    Date date = new Date(expire.longValue());
	                    Date now  = new Date();
	                    logger.debug(String.format("now : %d", System.currentTimeMillis()));
	                    if (expire == 0 || date.compareTo(now) > 0) {
	                    	forbidList_shadow.put(fields[0], expire);
	                    }
	                    
                    } catch (Exception ee) {
                    	logger.error(String.format("Date Convertion Error: [%s]", line));
                    	continue;
                    }
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
            
            try {
            	wr = new FileWriter(this.forbidFileName, false);
            	
            	for (String ip : forbidList_shadow.keySet()) {
            		wr.append(ip + "\t" + forbidList_shadow.get(ip) + "\n");
            	}
            	
            } catch (Exception e) {
            	logger.fatal("Fill the ip back failed.");
            } finally {
            	if (null != wr) {
            		try {
						wr.flush();
						wr.close();
					} catch (IOException e) { }
            	}
            }
        }
        
        // expect atomic exchange in object pointer ???
        {
        	allowList = allowList_shadow;
        	forbidList= forbidList_shadow;
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
     * 
     * judge specified address in IP dot format whether have privilege
     * to access resource.
     * 
     * need to have expiration limit, this implementation is simple for use first.
     * 
     * @param addr            IP format as: 8.8.8.8  or 10.7.7.%, 10.%
     * @return
     * 
     * true                    pass
     * false                   forbidden
     * 
     */
	public boolean judgeAccess(String addr) {
        if (addr == null) return false;
        addr = addr.trim();
        
        logger.debug(String.format("===========�� ClientAddress: [%s] under check now. [%s] [%d]", addr, this.toString(), order));
        Date now = new Date();
        
        if (Constant.ALLOWFIRST == order) {
        	if (allowList.keySet().contains(addr)) {
        		Long ms = allowList.get(addr);
        		Date ex = new Date(ms);
        		if (ex.compareTo(now) > 0)
        			return true;
        		else
	        		allowList.remove(addr);
        	}
            
        	if (forbidList.keySet().contains(addr)) {
        		Long ms = forbidList.get(addr);
        		Date ex = new Date(ms);
        		if (ex.compareTo(now) > 0) {
        			logger.debug("===== forbid by AllowFirst Rule, forbidList");
        			return false;
        		} else
	        		forbidList.remove(addr);
        	}
            
        } else if (Constant.FORBIDFIRST == order) {
        	if (forbidList.keySet().contains(addr)) {
        		Long ms = forbidList.get(addr);
        		Date ex = new Date(ms);
        		if (ex.compareTo(now) > 0) {
        			logger.debug("===== forbid by ForbidFirst Rule, forbidList");
        			return false;
        		} else
        			forbidList.remove(addr);
        	}
            
        	if (allowList.keySet().contains(addr)) {
        		Long ms = allowList.get(addr);
        		Date ex = new Date(ms);
        		if (ex.compareTo(now) > 0)
        			return true;
        		else
	        		allowList.remove(addr);
        	}
        	
   			logger.debug("===== forbid by ForbidFirst Rule, GeneralRule");
        	return false;
        }
		
        return true;
	}
}




