/******************************************************************************
 * Copyright (c) 2000-2018 Ericsson Telecom AB
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.html
 ******************************************************************************/
package org.eclipse.titan.designer.editors.configeditor.pages.compgroupmc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.titan.common.parsers.cfg.indices.ComponentSectionHandler.Component;

/**
 * @author Kristof Szabados
 * */
public final class ComponentSectionDragSourceListener implements DragSourceListener {

	private final TableViewer viewer;
	private final ComponentsSubPage componentsSubPage;

	public ComponentSectionDragSourceListener(final ComponentsSubPage componentsSubPage, final TableViewer viewer) {
		this.componentsSubPage = componentsSubPage;
		this.viewer = viewer;
	}

	@Override
	public void dragFinished(final DragSourceEvent event) {
		IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

		if (!selection.isEmpty()) {
			viewer.getTable().setRedraw(false);
			if (event.detail == DND.DROP_MOVE) {
				componentsSubPage.removeSelectedComponents();
			}
			viewer.getTable().setRedraw(true);
			viewer.refresh();
		}
	}

	@Override
	public void dragSetData(final DragSourceEvent event) {
		if (ComponentItemTransfer.getInstance().isSupportedType(event.dataType)) {
			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
			List<Component> items = new ArrayList<Component>();
			if (!selection.isEmpty()) {
				for (Iterator<?> it = selection.iterator(); it.hasNext();) {
					Object element = it.next();
					if (element instanceof Component) {
						items.add((Component) element);
					}
				}
				event.data = items.toArray(new Component[items.size()]);
			}
		}
	}

	@Override
	public void dragStart(final DragSourceEvent event) {
		IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		event.doit = !selection.isEmpty() && (selection.getFirstElement() instanceof Component);
	}

}
