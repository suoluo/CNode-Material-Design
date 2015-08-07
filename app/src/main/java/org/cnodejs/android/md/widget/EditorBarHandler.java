package org.cnodejs.android.md.widget;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.cnodejs.android.md.R;
import org.cnodejs.android.md.activity.MarkdownPreviewActivity;
import org.cnodejs.android.md.storage.SettingShared;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditorBarHandler {

    private Context context;
    private EditText edtContent;

    public EditorBarHandler(Context context, View editorBar, EditText edtContent) {
        this.context = context;
        this.edtContent = edtContent;
        ButterKnife.bind(this, editorBar);
    }

    /**
     * 加粗
     */
    @OnClick(R.id.editor_bar_btn_format_bold)
    protected void onBtnFormatBoldClick() {
        edtContent.requestFocus();
        edtContent.getText().insert(edtContent.getSelectionEnd(), "****");
        edtContent.setSelection(edtContent.getSelectionEnd() - 2);
    }

    /**
     * 倾斜
     */
    @OnClick(R.id.editor_bar_btn_format_italic)
    protected void onBtnFormatItalicClick() {
        edtContent.requestFocus();
        edtContent.getText().insert(edtContent.getSelectionEnd(), "**");
        edtContent.setSelection(edtContent.getSelectionEnd() - 1);
    }

    /**
     * 引用
     */
    @OnClick(R.id.editor_bar_btn_format_quote)
    protected void onBtnFormatQuoteClick() {
        edtContent.requestFocus();
        edtContent.getText().insert(edtContent.getSelectionEnd(), "\n\n> ");
    }

    /**
     * 无序列表
     */
    @OnClick(R.id.editor_bar_btn_format_list_bulleted)
    protected void onBtnFormatListBulletedClick() {
        edtContent.requestFocus();
        edtContent.getText().insert(edtContent.getSelectionEnd(), "\n\n- ");
    }

    /**
     * 有序列表 TODO 这里算法需要优化
     */
    @OnClick(R.id.editor_bar_btn_format_list_numbered)
    protected void onBtnFormatListNumberedClick() {
        edtContent.requestFocus();
        // 查找向上最近一个\n
        for (int n = edtContent.getSelectionEnd() - 1; n >= 0; n--) {
            char c = edtContent.getText().charAt(n);
            if (c == '\n') {
                try {
                    int index = Integer.parseInt(edtContent.getText().charAt(n + 1) + "");
                    if (edtContent.getText().charAt(n + 2) == '.' && edtContent.getText().charAt(n + 3) == ' ') {
                        edtContent.getText().insert(edtContent.getSelectionEnd(), "\n\n" + (index + 1) + ". ");
                        return;
                    }
                } catch (Exception e) {
                    // TODO 这里有问题是如果数字超过10，则无法检测，未来逐渐优化
                }
            }
        }
        // 没找到
        edtContent.getText().insert(edtContent.getSelectionEnd(), "\n\n1. ");
    }

    /**
     * 插入代码
     */
    @OnClick(R.id.editor_bar_btn_insert_code)
    protected void onBtnInsertCodeClick() {
        edtContent.requestFocus();
        edtContent.getText().insert(edtContent.getSelectionEnd(), "\n\n```\n\n\n\n```\n ");
        edtContent.setSelection(edtContent.getSelectionEnd() - 7);
    }

    /**
     * 插入链接
     */
    @OnClick(R.id.editor_bar_btn_insert_link)
    protected void onBtnInsertLinkClick() {
        new MaterialDialog.Builder(context)
                .iconRes(R.drawable.ic_insert_link_grey600_24dp)
                .title(R.string.add_link)
                .customView(R.layout.dialog_tool_insert_link, false)
                .positiveText(R.string.confirm)
                .negativeText(R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {

                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        View view = dialog.getCustomView();
                        EditText edtTitle = ButterKnife.findById(view, R.id.dialog_tool_insert_link_edt_title);
                        EditText edtLink = ButterKnife.findById(view, R.id.dialog_tool_insert_link_edt_link);

                        String insertText = " [" + edtTitle.getText() + "](" + edtLink.getText() + ") ";
                        edtContent.requestFocus();
                        edtContent.getText().insert(edtContent.getSelectionEnd(), insertText);
                    }

                })
                .show();
    }

    /**
     * 插入图片 TODO 目前没有图片上传接口
     */
    @OnClick(R.id.editor_bar_btn_insert_photo)
    protected void onBtnInsertPhotoClick() {
        edtContent.requestFocus();
        edtContent.getText().insert(edtContent.getSelectionEnd(), " ![](http://) ");
        edtContent.setSelection(edtContent.getSelectionEnd() - 11);
    }

    /**
     * 预览
     */
    @OnClick(R.id.editor_bar_btn_preview)
    protected void onBtnPreviewClick() {
        String content = edtContent.getText().toString();
        if (SettingShared.isEnableTopicSign(context)) { // 添加小尾巴
            content += "\n\n" + SettingShared.getTopicSignContent(context);
        }

        Intent intent = new Intent(context, MarkdownPreviewActivity.class);
        intent.putExtra("markdownText", content);
        context.startActivity(intent);
    }

}
