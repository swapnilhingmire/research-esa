package edu.kit.aifb.wikipedia.sql;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class LanglinksApiLanglinksMap implements ILanglinksMap {

	static Logger logger = Logger.getLogger( LanglinksApiLanglinksMap.class );
	
	ILanglinksApi llApi;
	
	@Autowired
	public void setLanglinksApi( ILanglinksApi llApi ) {
		this.llApi = llApi;;
	}

	public int map( int sourceId ) {
		return llApi.getTargetPageId( sourceId );
	}

}
