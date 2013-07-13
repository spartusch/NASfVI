/*
 * Copyright 2011 Stefan Partusch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.spartusch.nasfvi.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryParser.core.QueryNodeException;
import org.apache.lucene.queryParser.core.nodes.FieldQueryNode;
import org.apache.lucene.queryParser.core.nodes.ModifierQueryNode;
import org.apache.lucene.queryParser.core.nodes.QueryNode;
import org.apache.lucene.queryParser.core.nodes.SlopQueryNode;
import org.apache.lucene.queryParser.core.nodes.TokenizedPhraseQueryNode;
import org.apache.lucene.queryParser.core.processors.QueryNodeProcessorImpl;

import de.spartusch.StringMethods;

/**
 * Replaces {@link org.apache.lucene.queryParser.core.nodes.FieldQueryNode
 * FieldQueryNodes} with a {@link
 * org.apache.lucene.queryParser.core.nodes.SlopQueryNode sloppy} {@link
 * org.apache.lucene.queryParser.core.nodes.TokenizedPhraseQueryNode
 * TokenizedPhraseQueryNode}.
 * @author Stefan Partusch
 *
 */
public class FieldsCollapsingProcessor extends QueryNodeProcessorImpl {
	/** Names of the fields to replace. */
	private String[] fieldsToCollapse;
	/** Name of the TokenizedPhraseQueryNode to create. */
	private String collapseTo;
	/** Fields to replace. */
	private List<QueryNode> collapsedNodes;
	/** Fields to remove. A field is to be removed if it is replaced or
	 * if all of its children were removed. */
	private List<QueryNode> nodesToRemove;
	/** The slop to use. */
	private int slop;

	/** 
	 * @param fieldsToCollapse Names of the fields to replace
	 * @param collapseTo Name of the TokenizedPhraseQueryNode to replace
	 * the fields with
	 * @param slop The slop to use for the TokenizedPhraseQueryNode
	 * @see {@link org.apache.lucene.queryParser.core.nodes.SlopQueryNode
	 * SlopQueryNode}
	 */
	public FieldsCollapsingProcessor(final String[] fieldsToCollapse,
			final String collapseTo, final int slop) {
		this.fieldsToCollapse = fieldsToCollapse;
		this.collapseTo = collapseTo;
		this.slop = slop;
		collapsedNodes = new ArrayList<QueryNode>();
		nodesToRemove = new ArrayList<QueryNode>();
	}

	@Override
	public final QueryNode process(final QueryNode queryTree)
			throws QueryNodeException {
		collapsedNodes.clear();
		nodesToRemove.clear();

		// do preProcessNode and postProcessNode
		QueryNode root = super.process(queryTree);

		// create collapsed phrase query
		if (!collapsedNodes.isEmpty()) {
			TokenizedPhraseQueryNode phrase = new TokenizedPhraseQueryNode();
			phrase.setField(collapseTo);
			phrase.set(collapsedNodes);
			SlopQueryNode slopNode = new SlopQueryNode(phrase, slop);
			ModifierQueryNode mod = new ModifierQueryNode(slopNode,
					ModifierQueryNode.Modifier.MOD_REQ);

			if (root.getChildren().size() == 0) {
				// all children are collapsed/removed
				root = mod;
			} else {
				List<QueryNode> children = root.getChildren();
				children.add(mod);
			}
		}

		return root;
	}

	@Override
	protected final QueryNode preProcessNode(final QueryNode node)
			throws QueryNodeException {
		if (node instanceof FieldQueryNode) {
			FieldQueryNode fieldNode = (FieldQueryNode) node;

			if (StringMethods.equalsOneOf(fieldNode.getFieldAsString(),
					fieldsToCollapse)) {
				fieldNode.setField(collapseTo); // rename field
				collapsedNodes.add(fieldNode);
				nodesToRemove.add(fieldNode);
			}
		}

		return node;
	}

	@Override
	protected final QueryNode postProcessNode(final QueryNode node)
			throws QueryNodeException {
		List<QueryNode> children = node.getChildren();

		if (children != null && children.removeAll(nodesToRemove)) {
			if (children.size() == 0) {
				nodesToRemove.add(node);
			}
		}

		return node;
	}

	@Override
	protected final List<QueryNode>
			setChildrenOrder(final List<QueryNode> children)
			throws QueryNodeException {
		return children;
	}
}
