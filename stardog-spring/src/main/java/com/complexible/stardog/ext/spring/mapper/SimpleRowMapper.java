/*
* Copyright (c) the original authors
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.complexible.stardog.ext.spring.mapper;

import com.complexible.stardog.ext.spring.RowMapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;

/**
 * SimpleRowMapper
 * 
 * Implementation of the RowMapper interface that returns a Map of String,String
 * Useful for easily retrieving maps of String data
 * 
 * @author Al Baker
 * @author Clark and Parsia
 *
 */
public class SimpleRowMapper implements RowMapper<Map<String, String>> {

	@Override
	public Map<String, String> mapRow(BindingSet bindingSet) {
		Map<String, String> result = new HashMap<String, String>();
		Iterator<Binding> it = bindingSet.iterator();
		while (it.hasNext()) {
			Binding b = it.next();
			result.put(b.getName(), b.getValue().stringValue());
		}
		return result;
	}

}