/*******************************************************************************
 * Copyright (c) 2006, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Ed Swartz <ed.swartz@nokia.com> - initial API and implementation
 *         (bug 157203: [ltk] [patch] TextEditBasedChange/TextChange provides incorrect diff when one side is empty)
 *******************************************************************************/
package org.eclipse.jdt.ui.tests.refactoring.changes;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEditGroup;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IRegion;

import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.TextEditChangeGroup;

/**
 * Ensure that the diffs between text edits do not include
 * spurious context lines when source or target regions are
 * empty.  Otherwise, the current content and preview content
 * have differing affected ranges and spurious changes appear.
 */
public class TextDiffContentTest extends TestCase {

	private static final String MODIFIED_SOURCE_CONTENTS =
		"// my file\n"+
		"\n"+
		"CMyClass::CMyClass()\n"+
		"\t{\n"+
		"\tGoodCall();\n"+
		"\tDumbCall();\n"+
		"\t// [[[ begin\n"+
		"\tMagicCall();\n"+
		"\t// ]]] end\n"+
		"\t}\n"+
		"\n"+
		"// other stuff\n";

	public static Test suite() {
		return new TestSuite(TextDiffContentTest.class);
	}

	private DocumentChange fDocumentChange;

	private Document fDocument;

	private ReplaceEdit fEdit1;

	private ReplaceEdit fEdit2;

	private TextEditChangeGroup fChange1;

	private TextEditChangeGroup fChange2;

	private ReplaceEdit fEdit3;

	private TextEditChangeGroup fChange3;

	protected void setUp() throws Exception {
		super.setUp();
		fDocument = new Document(MODIFIED_SOURCE_CONTENTS);
		fDocumentChange = new DocumentChange("Changes to document", fDocument);

		// store one unified change for the document
		MultiTextEdit multiEdit = new MultiTextEdit();
		fDocumentChange.setEdit(multiEdit);

		int offset;
		TextEditGroup group;
		/////

		offset = MODIFIED_SOURCE_CONTENTS.indexOf("\t}\n");
		fEdit1 = new ReplaceEdit(
				offset, 0,
				"\tFinalCall();\n");

		group = new TextEditGroup("Change 1");
		group.addTextEdit(fEdit1);
		fChange1 = new TextEditChangeGroup(fDocumentChange, group);
		fDocumentChange.addTextEditChangeGroup(fChange1);
		multiEdit.addChild(fEdit1);

		/////
		offset = 0;
		fEdit2 = new ReplaceEdit(
				offset, 0,
				"// add comment\n");

		group = new TextEditGroup("Change 2");
		group.addTextEdit(fEdit2);
		fChange2 = new TextEditChangeGroup(fDocumentChange, group);
		fDocumentChange.addTextEditChangeGroup(fChange2);
		multiEdit.addChild(fEdit2);

		///
		offset = MODIFIED_SOURCE_CONTENTS.indexOf("\tDumb");
		int endOffset = MODIFIED_SOURCE_CONTENTS.indexOf("\t// [[[ begin", offset);
		fEdit3 = new ReplaceEdit(
				offset, endOffset - offset, "");

		group = new TextEditGroup("Change 3");
		group.addTextEdit(fEdit3);
		fChange3 = new TextEditChangeGroup(fDocumentChange, group);
		fDocumentChange.addTextEditChangeGroup(fChange3);
		multiEdit.addChild(fEdit3);

	}

	private String getSource(IRegion region, int context) throws CoreException {
		return fDocumentChange.getCurrentContent(region, true, context, new NullProgressMonitor());
	}
	private String getPreview(TextEditChangeGroup group, int context) throws CoreException {
		return fDocumentChange.getPreviewContent(
				new TextEditChangeGroup[] { group },
				group.getRegion(), true, context, new NullProgressMonitor());
	}

	public void testEmptySourceRangeNoContext() throws Exception {
		String src = getSource(fEdit1.getRegion(), 0);
		String preview = getPreview(fChange1, 0);
		assertEquals("", src);
		assertEquals("\tFinalCall();", preview);

	}
	public void testEmptySourceRangeNoContext2() throws Exception {
		String src = getSource(fEdit2.getRegion(), 0);
		String preview = getPreview(fChange2, 0);
		assertEquals("", src);
		assertEquals("// add comment", preview);

	}
	public void testEmptySourceRangeContext() throws Exception {
		String src = getSource(fEdit1.getRegion(), 2);
		String preview = getPreview(fChange1, 2);
		assertEquals("\tMagicCall();\n" +
				"\t// ]]] end\n" +
				"\t}\n",
				src);
		assertEquals("	MagicCall();\n" +
				"	// ]]] end\n" +
				"	FinalCall();\n" +
				"	}\n",
			preview);
	}
	public void testEmptySourceRangeContext2() throws Exception {
		String src = getSource(fEdit2.getRegion(), 2);
		String preview = getPreview(fChange2, 2);
		assertEquals("// my file\n",
				src);
		assertEquals("// add comment\n"+
				"// my file\n",
			preview);
	}

	public void testEmptyTargetRangeNoContext() throws Exception {
		String src = getSource(fEdit3.getRegion(), 0);
		String preview = getPreview(fChange3, 0);
		assertEquals("\tDumbCall();", src);
		assertEquals("", preview);

	}
	public void testEmptyTargetRangeContext() throws Exception {
		String src = getSource(fEdit3.getRegion(), 2);
		String preview = getPreview(fChange3, 2);
		assertEquals("\t{\n"+"\tGoodCall();\n"+
				"\tDumbCall();\n"+
				"\t// [[[ begin\n"+
				"\tMagicCall();",
				src);
		assertEquals("\t{\n"+"\tGoodCall();\n"+
				"\t// [[[ begin\n"+
				"\tMagicCall();",
				preview);

	}

}

