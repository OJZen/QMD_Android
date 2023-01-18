package com.qmd.jzen.utils

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.checkbox.checkBoxPrompt
import com.afollestad.materialdialogs.checkbox.getCheckBoxPrompt
import com.afollestad.materialdialogs.color.colorChooser
import com.afollestad.materialdialogs.files.folderChooser
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.customListAdapter
import com.afollestad.materialdialogs.list.listItems
import com.qmd.jzen.R
import java.io.File

/**
 * 这个类主要是给Java调用Material Dialog使用的，项目逐步转为kotlin这个类最终会废弃
 */
class QMDDialog {
    companion object {
        fun showFolderDialog(mContext: Context, path: String, callback: FolderDialogCallback) {
            val initialFolder = File(path)
            MaterialDialog(mContext).show {
                folderChooser(mContext, initialDirectory = initialFolder, emptyTextRes = R.string.text_folder_empty)
                { dialog, file ->
                    // File selected
                    callback.onResult(dialog, file)
                }
            }
        }

        fun showBaseDialog(mContext: Context, title: String, message: String) {
            MaterialDialog(mContext).show {
                title(text = title)
                message(text = message)
                positiveButton { }
            }
        }

        fun showBaseDialog(mContext: Context, title: String, message: String, callback: BaseDialogCallback) {
            MaterialDialog(mContext).show {
                title(text = title)
                message(text = message)
                positiveButton { dialog -> callback.onResult(dialog) }
                negativeButton { }
            }
        }

        fun showBaseDialog(mContext: Context, title: String, message: String, positiveText: String, negativeText: String, callback: BaseDialogCallback) {
            MaterialDialog(mContext).show {
                title(text = title)
                message(text = message)
                positiveButton(text = positiveText) { dialog -> callback.onResult(dialog) }
                negativeButton(text = negativeText)
            }
        }

        fun showDialogWithCheckBox(mContext: Context, title: String, message: String, checkBoxText: String, callback: DialogWithCheckBoxCallback) {
            MaterialDialog(mContext).show {
                title(text = title)
                message(text = message)
                checkBoxPrompt(text = checkBoxText) { }
                positiveButton { dialog -> callback.onResult(dialog.getCheckBoxPrompt().isChecked) }
                negativeButton { }
            }
        }

        fun showListDialog(mContext: Context, title: String, list: Int, callback: ListDialogCallback) {
            MaterialDialog(mContext).show {
                title(text = title)
                listItems(list) { dialog, index, text ->
                    // Invoked when the user taps an item
                    callback.onResult(dialog, index)
                }
            }
        }

        fun showColorDialog(mContext: Context, title: String, initialColor: Int, colors: IntArray, callback: ColorDialogCallback) {

            MaterialDialog(mContext).show {
                title(text = title)
                colorChooser(
                        initialSelection = initialColor,
                        colors = colors,
                        allowCustomArgb = true
                ) { dialog, color ->
                    // Use color integer
                    callback.onResult(dialog, color)
                }
                positiveButton()
                negativeButton()
            }
        }

        fun showInputDialog(mContext: Context, title: String, hintText: String, callback: InputDialogCallback) {
            MaterialDialog(mContext).show {
                title(text = title)
                input(hint = hintText)
                { dialog, text ->
                    callback.onResult(dialog, text.toString())
                }
                positiveButton()
                negativeButton()
            }
        }

        fun showCustomListDialog(mContext: Context, title: String, adapter: RecyclerView.Adapter<*>, callback: BaseDialogCallback): MaterialDialog {
            return MaterialDialog(mContext).show {
                customListAdapter(adapter)
                title(text = title)
                positiveButton { dialog -> callback.onResult(dialog) }
            }
        }
    }

    interface FolderDialogCallback {
        fun onResult(dialog: MaterialDialog, file: File)
    }

    interface BaseDialogCallback {
        fun onResult(dialog: MaterialDialog)
    }

    interface DialogWithCheckBoxCallback {
        fun onResult(checked: Boolean)
    }

    interface ListDialogCallback {
        fun onResult(dialog: MaterialDialog, index: Int)
    }

    interface ColorDialogCallback {
        fun onResult(dialog: MaterialDialog, color: Int)
    }

    interface InputDialogCallback {
        fun onResult(dialog: MaterialDialog, text: String)
    }
}