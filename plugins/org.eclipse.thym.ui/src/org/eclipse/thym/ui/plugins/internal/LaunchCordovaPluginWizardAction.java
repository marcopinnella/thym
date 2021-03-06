/*******************************************************************************
 * Copyright (c) 2013, 2014 Red Hat, Inc. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * 	Contributors:
 * 		 Red Hat Inc. - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.thym.ui.plugins.internal;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.thym.core.HybridProject;
import org.eclipse.thym.ui.HybridUI;
import org.eclipse.thym.ui.config.internal.ConfigEditor;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class LaunchCordovaPluginWizardAction extends Action {

	private final ImageDescriptor icon = HybridUI.getImageDescriptor(HybridUI.PLUGIN_ID, "/icons/obj16/plug16_obj.png");
	private ConfigEditor configEditor;
	private int initialTab = CordovaPluginSelectionPage.PLUGIN_SOURCE_REGISTRY;
	
	public LaunchCordovaPluginWizardAction() {
		super("Install Cordova Plug-in");
		setImageDescriptor(icon);
	}
	
	/**
	 * Causes the launched Wizard to be initialized and fixed with the 
	 * project that the resource for the editor is located in 
	 * @param editor
	 */
	public LaunchCordovaPluginWizardAction(ConfigEditor editor) {
		this();
		this.configEditor = editor;
	}
	
	/**
	 * Causes the launched Wizard to be initialized and fixed with the 
	 * project that the resource for the editor is located in 
	 * @param editor
	 */
	public LaunchCordovaPluginWizardAction(ConfigEditor editor, int initialTab) {
		this(editor);
		this.initialTab = initialTab;
	}
	
	
	
	
	/*
	 * @see IAction.run()
	 */
	public void run() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		
		IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
		ISelection selection = workbenchWindow.getSelectionService().getSelection();
		
		if(selection == null || selection.isEmpty()){
			IWorkbenchPage page = workbenchWindow.getActivePage();
			if(page != null && page.getActiveEditor() instanceof ConfigEditor){
				ConfigEditor editor = (ConfigEditor)page.getActiveEditor();
				IFileEditorInput input = (IFileEditorInput) editor.getEditorInput();
				selection= new StructuredSelection(input.getFile().getProject());
			}
		}
	
		IStructuredSelection selectionToPass = null;
		if (selection instanceof IStructuredSelection)
			selectionToPass = (IStructuredSelection) selection;
		else
			selectionToPass = StructuredSelection.EMPTY;
	
		CordovaPluginWizard wizard = new CordovaPluginWizard();
		if(this.configEditor == null ){
			wizard.init(workbench, selectionToPass);
		}else{
			IResource resource = (IResource)configEditor.getEditorInput().getAdapter(IResource.class);
			if(resource != null ){
				HybridProject project = HybridProject.getHybridProject(resource.getProject());
				if(project != null){
					wizard.init(project,initialTab);
				}else{
					wizard.init(workbench, selectionToPass);
				}
			}
		}
		WizardDialog dialog = new WizardDialog(workbench.getActiveWorkbenchWindow().getShell(), wizard);
		dialog.setMinimumPageSize(550, 450);//TODO: needs a more clever way to set this values
		dialog.open();
	}
}
