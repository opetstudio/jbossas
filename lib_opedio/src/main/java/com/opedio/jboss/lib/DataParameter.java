package com.opedio.jboss.lib;

import com.solusi247.poin.data.PoinApiAccess;

import java.io.Serializable;
import java.util.Map;
//import com.solusi247.poin.iprestrict.InjectApiIpRestrictSingleton;

public class DataParameter  implements Serializable {
	static final long serialVersionUID = 1L;
	long _msg_id ;  
	String _msisdn; 
	String _msisdn_trim;
	String _trxid;
	String _keyword;
	String _ip_addr;
	String _ip_f5;
	String _username;
	String _password;
	String _callback;
	String _channel;
	String _json_str;
	String _data_discus_json_str;
	
	//sync diskusi
	String _email;
	String _maximal_reload_newer;
	String _count_curr_rec;
	String _newer_modifiedon;
	String _current_lesson_id;
	String _discus_msg;
	
	
	private Map<String,PoinApiAccess> _access_list  = null;

	public DataParameter() {
//		_access_list = InjectApiIpRestrictSingleton.get_access_list_ip();
	}


	public long get_msg_id() {
		return _msg_id;
	}


	public void set_msg_id(long _msg_id) {
		this._msg_id = _msg_id;
		
	}


	public String get_msisdn() {
		return _msisdn;
	}
	


	public void set_msisdn(String _msisdn) {
		this._msisdn = _msisdn;
		set_msisdn_trim(_msisdn);
	}
	
	public String get_msisdn_trim() {
		return _msisdn_trim;
	}
	
	public void set_msisdn_trim(String _p_msisdn) {
		if(_p_msisdn != null){
			if (_p_msisdn.startsWith("62")) {
				_p_msisdn = _p_msisdn.substring(2);
			} else if (_p_msisdn.startsWith("08")) {
				_p_msisdn = _p_msisdn.substring(1);
			} else if (_p_msisdn.startsWith("+62")) {
				_p_msisdn = _p_msisdn.substring(3);
			}
		}
		this._msisdn_trim = _p_msisdn;
	}


	public String get_trxid() {
		return _trxid;
	}


	public void set_trxid(String _trxid) {
		this._trxid = _trxid;
	}


	public String get_keyword() {
		return _keyword;
	}


	public void set_keyword(String _keyword) {
		this._keyword = _keyword;
	}


	public String get_ip_addr() {
		return _ip_addr;
	}


	public void set_ip_addr(String _ip_addr) {
		this._ip_addr = _ip_addr;
	}


	public String get_ip_f5() {
		return _ip_f5;
	}


	public void set_ip_f5(String _ip_f5) {
		this._ip_f5 = _ip_f5;
	}


	public String get_username() {
		return _username;
	}


	public void set_username(String _username) {
		this._username = _username;
	}


	public String get_password() {
		return _password;
	}


	public void set_password(String _password) {
		this._password = _password;
	}


	public String get_callback() {
		return _callback;
	}


	public void set_callback(String _callback) {
		this._callback = _callback;
	}


	public String get_channel() {
		return _channel;
	}


	public void set_channel(String _channel) {
		this._channel = _channel;
	}
	
	public boolean isAuth(){
		/*PoinApiAccess acc = (PoinApiAccess)this._access_list.get(this.get_ip_addr());
		if (acc!=null 
				&& acc.get_ip_addr().equals(this.get_ip_addr())
				&& acc.get_pPass().equals(this.get_password())
//				&& acc.get_pSubj().equals(pSubj)
				&& acc.get_pUid().equals(this.get_username())
				){
			return true;
		}*/
		return false;
	}
	//untuk manual redeem
	public boolean isParamComplete(){
		if(this.get_keyword() == null || "".equalsIgnoreCase(this.get_keyword())) return false;
		if(this.get_msisdn() == null || "".equalsIgnoreCase(this.get_msisdn())) return false;
		if(this.get_trxid() == null || "".equalsIgnoreCase(this.get_trxid())) return false;
//		if(this.get_channel() == null || "".equalsIgnoreCase(this.get_channel())) return false;
		return true;
	}
	//untuk GetTierAmount
	public boolean isParamComplete2(){
//		if(this.get_channel() == null || "".equalsIgnoreCase(this.get_channel())) return false;
		if(this.get_msisdn() == null || "".equalsIgnoreCase(this.get_msisdn())) return false;
		if(this.get_trxid() == null || "".equalsIgnoreCase(this.get_trxid())) return false;
		return true;
	}


	public String get_json_str() {
		return _json_str;
	}


	public void set_json_str(String _json_str) {
		this._json_str = _json_str;
	}


	public String get_data_discus_json_str() {
		return _data_discus_json_str;
	}


	public void set_data_discus_json_str(String _data_discus_json_str) {
		this._data_discus_json_str = _data_discus_json_str;
	}


	public String get_email() {
		return _email;
	}


	public void set_email(String _email) {
		this._email = _email;
	}


	public String get_maximal_reload_newer() {
		return _maximal_reload_newer;
	}


	public void set_maximal_reload_newer(String _maximal_reload_newer) {
		this._maximal_reload_newer = _maximal_reload_newer;
	}


	public String get_count_curr_rec() {
		return _count_curr_rec;
	}


	public void set_count_curr_rec(String _count_curr_rec) {
		this._count_curr_rec = _count_curr_rec;
	}


	public String get_newer_modifiedon() {
		return _newer_modifiedon;
	}


	public void set_newer_modifiedon(String _newer_modifiedon) {
		this._newer_modifiedon = _newer_modifiedon;
	}


	public String get_current_lesson_id() {
		return _current_lesson_id;
	}


	public void set_current_lesson_id(String _current_lesson_id) {
		this._current_lesson_id = _current_lesson_id;
	}


	public String get_discus_msg() {
		return _discus_msg;
	}


	public void set_discus_msg(String _discus_msg) {
		this._discus_msg = _discus_msg;
	}
	
	
	
	
	
	

	

	
	
	

}
