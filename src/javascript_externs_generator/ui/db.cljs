(ns javascript-externs-generator.ui.db)

(def default-state {:loaded-urls #{}
                    :externed-namespaces {}
                    :current-namespace nil
                    :url-text ""
                    :namespace-text ""
                    :alert {:heading ""
                            :text ""}
                    :loading-js false})
